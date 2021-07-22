package ru.skillbranch.skillarticles.data

import ru.skillbranch.skillarticles.data.EntityGenerator.generateArticleRes
import ru.skillbranch.skillarticles.data.EntityGenerator.generateComments
import ru.skillbranch.skillarticles.data.remote.res.CommentRes
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.remote.res.ArticleContentRes
import ru.skillbranch.skillarticles.data.remote.res.ArticleRes
import ru.skillbranch.skillarticles.extensions.data.toArticleContentRes
import java.lang.Thread.sleep
import java.util.*
import kotlin.math.abs

object NetworkDataHolder {

    private val networkArticleItems: List<ArticleRes> = generateArticleRes(200)

    val commentsData: Map<String, MutableList<CommentRes>> by lazy {
        networkArticleItems.associate { article ->
            article.data.id to generateComments(
                article.data.id,
                article.counts.comments
            ) as MutableList
        }
    }

    fun findArticlesItem(start: Int = 0, size: Int): List<ArticleRes> {
        return networkArticleItems.drop(start)
            .take(size)
            .apply {
                sleep(100)
            }
    }

    fun loadArticleContent(articleId: String): ArticleContentRes =
        articleItems[articleId.toInt() % 10].copy(id = articleId ).toArticleContentRes()



    fun loadComments(slug: String?, size: Int, articleId: String): List<CommentRes> {
        val commentsData = commentsData
            .getOrElse(articleId) { mutableListOf() }

        val list = when {
            slug == null -> commentsData.take(size)

            size > 0 -> commentsData.dropWhile { it.slug != slug }
                .drop(1)
                .take(size)

            size < 0 -> commentsData
                .dropLastWhile { it.slug != slug }
                .dropLast(1)
                .takeLast(abs(size))

            else -> emptyList()
        }
        return list
    }
}





