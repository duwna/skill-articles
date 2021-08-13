package ru.skillbranch.skillarticles.viewmodels.articles

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.skillbranch.skillarticles.data.local.entities.ArticleItem
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.data.remote.err.NoNetworkError
import ru.skillbranch.skillarticles.data.repositories.ArticleFilter
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ArticlesViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: ArticlesRepository
) : BaseViewModel<ArticlesState>(handle, ArticlesState()) {

    private var isLoadingInitial = false
    private var isLoadingAfter = false

    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .setPrefetchDistance(30)
            .setInitialLoadSizeHint(50)
            .build()
    }

    private val listData = Transformations.switchMap(state) {
        val filter = it.toArticleFilter()
        return@switchMap buildPagedList(repository.rawQueryArticles(filter))
    }

    fun observeList(
        owner: LifecycleOwner,
        isBookmark: Boolean = false,
        onChange: (list: PagedList<ArticleItem>) -> Unit
    ) {
        updateState { it.copy(isBookmark = isBookmark) }
        listData.observe(owner, Observer(onChange))
    }


    fun observeTags(lifecycleOwner: LifecycleOwner, onChange: (List<String>) -> Unit) {
        repository.findTags().observe(lifecycleOwner, Observer(onChange))
    }

    fun observeCategories(owner: LifecycleOwner, onChange: (List<CategoryData>) -> Unit) {
        repository.findCategoriesData().observe(owner, Observer(onChange))
    }

    private fun buildPagedList(
        dataFactory: DataSource.Factory<Int, ArticleItem>
    ): LiveData<PagedList<ArticleItem>> {
        val builder = LivePagedListBuilder(
            dataFactory,
            listConfig
        )

        //if all articles
        if (isEmptyFilter()) {
            builder.setBoundaryCallback(
                ArticlesBoundaryCallback(
                    ::zeroLoadingHandle,
                    ::itemAtEndHandle
                )
            )
        }

        return builder
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    private fun isEmptyFilter(): Boolean = currentState.searchQuery.isNullOrEmpty()
            && !currentState.isBookmark
            && currentState.selectedCategories.isEmpty()
            && !currentState.isHashtagSearch

    private fun itemAtEndHandle(lastLoadArticle: ArticleItem) {
        if (isLoadingAfter) return
        else isLoadingAfter = true

        launchSafety(null, { isLoadingAfter = false }) {
            repository.loadArticlesFromNetwork(
                start = lastLoadArticle.id,
                size = listConfig.pageSize
            )
        }
    }

    private fun zeroLoadingHandle() {
        if (isLoadingInitial) return
        else isLoadingInitial = true

        launchSafety(null, { isLoadingInitial = false }) {
            repository.loadArticlesFromNetwork(
                start = null,
                size = listConfig.initialLoadSizeHint
            )
        }
    }

    fun handleSearch(query: String?) {
        query ?: return
        updateState { it.copy(searchQuery = query, isHashtagSearch = query.startsWith("#", true)) }
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch) }
    }

    fun handleToggleBookmark(articleId: String) {
        launchSafety(
            { throwable ->
                when (throwable) {
                    is NoNetworkError -> notify(Notify.TextMessage("Network not available, failed to fetch article"))
                    else -> notify(Notify.ErrorMessage(throwable.message ?: "Something wrong"))
                }
                throwable.printStackTrace()
            }
        ) {
            val isBookmarked = repository.toggleBookmark(articleId)
            if (isBookmarked) repository.fetchArticleContent(articleId)
            else repository.removeArticleContent(articleId)
        }
    }

    fun handleSuggestion(tag: String) {
        launchSafety { repository.incrementTagUseCount(tag) }
    }

    fun applyCategories(selectedCategories: List<String>) {
        updateState { it.copy(selectedCategories = selectedCategories) }
    }

    fun refresh() {
        launchSafety {
            val lastArticleId = repository.findLastArticleId()
            val count = repository.loadArticlesFromNetwork(
                start = lastArticleId,
                size = if (lastArticleId == null) listConfig.initialLoadSizeHint
                else -listConfig.pageSize
            )
            notify(Notify.TextMessage("Load $count new articles"))
        }
    }


}

fun ArticlesState.toArticleFilter(): ArticleFilter = ArticleFilter(
    search = searchQuery,
    isBookmark = isBookmark,
    categories = selectedCategories,
    isHashtag = isHashtagSearch
)

data class ArticlesState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true,
    val isBookmark: Boolean = false,
    val selectedCategories: List<String> = emptyList(),
    val isHashtagSearch: Boolean = false
) : IViewModelState


class ArticlesBoundaryCallback(
    private val zeroLoadingHandle: () -> Unit,
    private val itemAtEndHandle: (ArticleItem) -> Unit

) : PagedList.BoundaryCallback<ArticleItem>() {
    override fun onZeroItemsLoaded() {
        //Storage is empty
        zeroLoadingHandle()
    }

    override fun onItemAtEndLoaded(itemAtEnd: ArticleItem) {
        //user scroll down -> need load more items
        itemAtEndHandle(itemAtEnd)
    }
}