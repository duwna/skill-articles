package ru.skillbranch.skillarticles.extensions

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*

fun View.setMarginOptionally(
    left: Int = marginLeft,
    top: Int = marginTop,
    right: Int = marginRight,
    bottom: Int = marginBottom
) = updateLayoutParams<CoordinatorLayout.LayoutParams> {
    setMargins(left, top, right, bottom)
}

fun View.setPaddingOptionally(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}
