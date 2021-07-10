package ru.skillbranch.skillarticles.viewmodels.bookmarks

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.data.repositories.ArticlesDataFactory
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import java.util.concurrent.Executors

class BookmarksViewModel(handle: SavedStateHandle) :
    BaseViewModel<BookmarksState>(handle, BookmarksState()) {
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .setPrefetchDistance(30)
            .setInitialLoadSizeHint(50)
            .build()
    }
    private val repository = ArticlesRepository

    private val listData: LiveData<PagedList<ArticleItemData>> = Transformations.switchMap(state) {
        when {
            it.isSearch && !it.searchQuery.isNullOrBlank() ->
                buildPagedList(repository.searchBookmarks(it.searchQuery))
            else -> buildPagedList(repository.bookmarkArticles())
        }
    }

    fun observeList(owner: LifecycleOwner, onChange: (list: PagedList<ArticleItemData>) -> Unit) {
        listData.observe(owner, onChange)
    }

    fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch) }
    }

    fun handleQuery(query: String?) {
        query ?: return
        updateState { it.copy(searchQuery = query) }
    }

    private fun buildPagedList(factory: ArticlesDataFactory):
            LiveData<PagedList<ArticleItemData>> {

        return LivePagedListBuilder(factory, listConfig)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun handleToggleBookmark(id: String, isChecked: Boolean) {
        repository.updateBookmark(id, isChecked)
        listData.value?.dataSource?.invalidate()
    }

}

data class BookmarksState(
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val isLoading: Boolean = true
) : IViewModelState
