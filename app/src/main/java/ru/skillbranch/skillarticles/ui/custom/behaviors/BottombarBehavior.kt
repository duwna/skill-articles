package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.skillbranch.skillarticles.extensions.actionBarHeight
import ru.skillbranch.skillarticles.ui.custom.Bottombar


class BottombarBehavior(
    context: Context,
    attrs: AttributeSet? = null
) : CoordinatorLayout.Behavior<Bottombar>(context, attrs) {

    private val actionBarHeight = context.actionBarHeight()

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: Bottombar,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean = true


    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        bottombar: Bottombar,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {

        val expectedTranslation = bottombar.translationY + dyConsumed

        when {
            expectedTranslation in 0f..actionBarHeight ->
                bottombar.translationY = expectedTranslation

            expectedTranslation > actionBarHeight ->
                bottombar.translationY = actionBarHeight

            expectedTranslation < 0 ->
                bottombar.translationY = 0f
        }

        super.onNestedScroll(
            coordinatorLayout,
            bottombar,
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
        bottombar: Bottombar,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {

        bottombar.animate()
            .translationY(if (velocityY > 0) actionBarHeight else 0f)
            .duration = 200

        return super.onNestedFling(coordinatorLayout, bottombar, target, velocityX, velocityY, consumed)
    }
}