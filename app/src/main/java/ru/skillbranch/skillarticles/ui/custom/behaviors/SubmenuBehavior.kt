package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.skillbranch.skillarticles.extensions.actionBarHeight
import ru.skillbranch.skillarticles.extensions.dpToPx
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu

class SubmenuBehavior(
    context: Context,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<ArticleSubmenu>(context, attrs) {

    // width + margin
    private val subMenuWidth = context.dpToPx(200 + 8)
    // to hide bottombar and submenu at the same time
    private val relation = subMenuWidth / context.actionBarHeight()

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: ArticleSubmenu,
        directTargetChild: View,
        target: View,
        axes: Int
    ): Boolean = true

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        articleSubmenu: ArticleSubmenu,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {

        val expectedTranslation = articleSubmenu.translationX + dyConsumed * relation

        when {
            expectedTranslation in 0f..subMenuWidth ->
                articleSubmenu.translationX = expectedTranslation

            expectedTranslation > subMenuWidth ->
                articleSubmenu.translationX = subMenuWidth

            expectedTranslation < 0 ->
                articleSubmenu.translationX = 0f
        }

        super.onNestedScroll(
            coordinatorLayout,
            articleSubmenu,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
    }

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        articleSubmenu: ArticleSubmenu,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {

        articleSubmenu.animate()
            .translationX(if (velocityY > 0) subMenuWidth else 0f)
            .duration = 200

        return super.onNestedFling(
            coordinatorLayout,
            articleSubmenu,
            target,
            velocityX,
            velocityY,
            consumed
        )
    }
}