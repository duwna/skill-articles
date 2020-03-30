package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.withTranslation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx

@SuppressLint("ViewConstructor")
class MarkdownTextView constructor(
    context: Context,
    fontSize: Float,
    helper: SearchBgHelper? = null
) : AppCompatTextView(context, null, 0), IMarkdownView {

    override var fontSize = fontSize
        set(value) {
            textSize = value
            field = value
        }

    private val color = context.attrValue(R.attr.colorOnBackground)
    private val focusRect = Rect()

    override val spannableContent: Spannable
        get() = text as Spannable

    @SuppressLint("VisibleForTests")
    private val searchBgHelper = SearchBgHelper(context) { top, bottom ->
        focusRect.set(
            0,
            top - context.dpToIntPx(56),
            width,
            bottom + context.dpToIntPx(56)
        )
        requestRectangleOnScreen(focusRect, false)
    }

    init {
        setTextColor(color)
        textSize = fontSize
        movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("VisibleForTests")
    override fun onDraw(canvas: Canvas) {
        if (text is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                searchBgHelper.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }
}