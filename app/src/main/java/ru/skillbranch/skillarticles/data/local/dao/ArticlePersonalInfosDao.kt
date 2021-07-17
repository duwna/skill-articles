package ru.skillbranch.skillarticles.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.skillbranch.skillarticles.data.local.entities.ArticlePersonalInfo

@Dao
interface ArticlePersonalInfosDao : BaseDao<ArticlePersonalInfo> {

    @Query("""
        UPDATE article_personal_infos SET is_like = NOT is_like, updated_at = CURRENT_TIMESTAMP
        WHERE article_id = :articleId
    """)
    fun toggleLike(articleId: String): Int

    @Query("""
        UPDATE article_personal_infos SET is_bookmark = NOT is_bookmark, updated_at = CURRENT_TIMESTAMP
        WHERE article_id = :articleId
    """)
    fun toggleBookmark(articleId: String): Int

    @Transaction
    fun toggleBookmarkOrInsert(articleId: String) {
        if (toggleBookmark(articleId) == 0) {
            insert(ArticlePersonalInfo(articleId, isBookmark = true))
        }
    }

    @Transaction
    fun toggleLikeOrInsert(articleId: String) {
        if (toggleLike(articleId) == 0) {
            insert(ArticlePersonalInfo(articleId, isLike = true))
        }
    }

}