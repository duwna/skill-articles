package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.local.dao.ArticleContentsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticleCountsDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlePersonalInfosDao
import ru.skillbranch.skillarticles.data.local.dao.ArticlesDao
import ru.skillbranch.skillarticles.data.local.entities.ArticleFull
import ru.skillbranch.skillarticles.data.models.AppSettings
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.err.NoNetworkError
import ru.skillbranch.skillarticles.data.remote.req.MessageReq
import ru.skillbranch.skillarticles.data.remote.res.CommentRes
import ru.skillbranch.skillarticles.extensions.data.toArticleContent
import javax.inject.Inject

interface IArticleRepository : IRepository {
    fun findArticle(articleId: String): LiveData<ArticleFull>
    fun getAppSettings(): LiveData<AppSettings>
    suspend fun toggleLike(articleId: String): Boolean
    suspend fun toggleBookmark(articleId: String): Boolean
    fun isAuth(): LiveData<Boolean>
    suspend fun sendMessage(articleId: String, message: String, answerToMessageId: String?)
    fun loadAllComments(
        articleId: String,
        totalCount: Int,
        errHandler: (Throwable) -> Unit
    ): CommentsDataFactory

    suspend fun decrementLike(articleId: String)
    suspend fun incrementLike(articleId: String)
    fun updateSettings(settings: AppSettings)
    suspend fun fetchArticleContent(articleId: String)
    fun findArticleCommentCount(articleId: String): LiveData<Int>
    suspend fun addBookmark(articleId: String)
    suspend fun removeBookmark(articleId: String)
}

class ArticleRepository @Inject constructor(
    private val network: RestService,
    private val preferences: PrefManager,
    private var articlesDao: ArticlesDao,
    private var articlesPersonalDao: ArticlePersonalInfosDao,
    private var articlesCountsDao: ArticleCountsDao,
    private var articlesContentDao: ArticleContentsDao
) : IArticleRepository {

    override fun findArticle(articleId: String): LiveData<ArticleFull> {
        return articlesDao.findFullArticle(articleId)
    }

    override fun getAppSettings(): LiveData<AppSettings> = preferences.appSettings

    override suspend fun toggleLike(articleId: String): Boolean {
        return articlesPersonalDao.toggleLikeOrInsert(articleId)
    }

    override suspend fun toggleBookmark(articleId: String): Boolean {
        return articlesPersonalDao.toggleBookmarkOrInsert(articleId)
    }

    override suspend fun fetchArticleContent(articleId: String) {
        val content = network.loadArticleContent(articleId)
        articlesContentDao.insert(content.toArticleContent())
    }

    override fun findArticleCommentCount(articleId: String): LiveData<Int> {
        return articlesCountsDao.getCommentsCount(articleId)
    }

    override fun isAuth(): LiveData<Boolean> = preferences.isAuthLive

    override fun loadAllComments(
        articleId: String,
        totalCount: Int,
        errHandler: (Throwable) -> Unit
    ) =
        CommentsDataFactory(
            itemProvider = network,
            articleId = articleId,
            totalCount = totalCount,
            errHandler = errHandler
        )

    override suspend fun decrementLike(articleId: String) {

        if (preferences.accessToken.isEmpty()) {
            articlesCountsDao.decrementLike(articleId)
            return
        }

        try {
            val res = network.decrementLike(articleId, preferences.accessToken)
            articlesCountsDao.updateLike(articleId, res.likeCount)
        } catch (t: Throwable) {
            articlesCountsDao.decrementLike(articleId)
            throw t
        }
    }


    override suspend fun incrementLike(articleId: String) {

        if (preferences.accessToken.isEmpty()) {
            articlesCountsDao.incrementLike(articleId)
            return
        }

        try {
            val res = network.incrementLike(articleId, preferences.accessToken)
            articlesCountsDao.updateLike(articleId, res.likeCount)
        } catch (t: Throwable) {
            if (t is NoNetworkError) {
                articlesCountsDao.incrementLike(articleId)
                return
            }
            throw t
        }
    }


    override suspend fun addBookmark(articleId: String) {
        if (preferences.accessToken.isEmpty()) return
        try {
            network.addBookmark(articleId, preferences.accessToken)
        } catch (e: Throwable) {
            if (e is NoNetworkError) return
            throw e
        }
    }

    override suspend fun removeBookmark(articleId: String) {
        if (preferences.accessToken.isEmpty()) return
        try {
            network.removeBookmark(articleId, preferences.accessToken)
        } catch (e: Throwable) {
            if (e is NoNetworkError) return
            throw e
        }
    }


    override fun updateSettings(settings: AppSettings) {
        preferences.isBigText = settings.isBigText
        preferences.isDarkMode = settings.isDarkMode
    }

    override suspend fun sendMessage(
        articleId: String,
        message: String,
        answerToMessageId: String?
    ) {
        val (_, messageCount) = network.sendMessage(
            articleId,
            MessageReq(message, answerToMessageId),
            preferences.accessToken
        )
        articlesCountsDao.updateCommentsCount(articleId, messageCount)
    }

    suspend fun refreshCommentsCount(articleId: String) {
        val counts = network.loadArticleCounts(articleId)
        articlesCountsDao.updateCommentsCount(articleId, counts.comments)
    }


}

class CommentsDataFactory(
    private val itemProvider: RestService,
    private val articleId: String,
    private val totalCount: Int,
    private val errHandler: (Throwable) -> Unit
) : DataSource.Factory<String?, CommentRes>() {
    override fun create(): DataSource<String?, CommentRes> =
        CommentsDataSource(itemProvider, articleId, totalCount, errHandler)

}

class CommentsDataSource(
    private val itemProvider: RestService,
    private val articleId: String,
    private val totalCount: Int,
    private val errHandler: (Throwable) -> Unit
) : ItemKeyedDataSource<String, CommentRes>() {

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<CommentRes>
    ) {
        try {
            val result = itemProvider.loadComments(
                articleId, params.requestedInitialKey, params.requestedLoadSize
            ).execute()

            callback.onResult(
                if (totalCount > 0) result.body()!! else emptyList(),
                0,
                totalCount
            )
        } catch (e: Throwable) {
            errHandler(e)
        }

    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<CommentRes>) {
        try {
            val result = itemProvider.loadComments(
                articleId, params.key, params.requestedLoadSize
            ).execute()
            callback.onResult(result.body()!!)
        } catch (e: Throwable) {
            errHandler(e)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<CommentRes>) {
        try {
            val result = itemProvider.loadComments(
                articleId, params.key, -params.requestedLoadSize
            ).execute()
            callback.onResult(result.body()!!)
        } catch (e: Throwable) {
            errHandler(e)
        }
    }

    override fun getKey(item: CommentRes): String = item.id

}
