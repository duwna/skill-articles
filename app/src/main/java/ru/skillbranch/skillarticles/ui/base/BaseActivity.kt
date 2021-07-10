package ru.skillbranch.skillarticles.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.circleCropTransform
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

abstract class BaseActivity<T : BaseViewModel<out IViewModelState>> : AppCompatActivity() {
    protected abstract val viewModel: T
    protected abstract val layout: Int

    lateinit var navController: NavController

    val toolbarBuilder = ToolbarBuilder()
    val bottombarBuilder = BottombarBuilder()

    abstract fun subscribeOnState(state: IViewModelState)
    abstract fun renderNotification(notify: Notify)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setSupportActionBar(toolbar)

        viewModel.observeState(this) { subscribeOnState(it) }
        viewModel.observeNotifications(this) { renderNotification(it) }
        viewModel.observeNavigation(this) { subscribeOnNavigation(it) }

        navController = findNavController(R.id.nav_host_fragment)
    }

    private fun subscribeOnNavigation(command: NavigationCommand) {
        when (command) {
            is NavigationCommand.To -> navController.navigate(
                command.destination, command.args,
                command.options, command.extras
            )
            is NavigationCommand.FinishLogin -> {
                navController.navigate(R.id.finish_login)
                if (command.privateDestination != null) navController.navigate(command.privateDestination)
            }
            is NavigationCommand.StartLogin -> navController.navigate(
                R.id.start_login,
                bundleOf("private_destination" to (command.privateDestination ?: -1))
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.saveState()
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel.restoreState()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

class ToolbarBuilder {
    var title: String? = null
    var subtitle: String? = null
    var logo: String? = null
    var visibility: Boolean = true
    val items: MutableList<MenuItemHolder> = mutableListOf()

    fun setTitle(title: String) = apply { this.title = title }
    fun setSubtitle(subTitle: String) = apply { this.subtitle = subTitle }
    fun setLogo(logo: String) = apply { this.logo = logo }
    fun setVisibility(isVisible: Boolean) = apply { this.visibility = isVisible }
    fun addMenuItem(item: MenuItemHolder) = apply { this.items.add(item) }

    fun invalidate() = apply {
        title = null
        subtitle = null
        logo = null
        visibility = true
        items.clear()
    }

    fun prepare(prepareFn: (ToolbarBuilder.() -> Unit)?) = apply { prepareFn?.invoke(this) }

    fun build(context: FragmentActivity) {

        context.appbar?.setExpanded(true, true)

        context.toolbar?.run {
            if (this@ToolbarBuilder.title != null) title = this@ToolbarBuilder.title
            subtitle = this@ToolbarBuilder.subtitle
            if (this@ToolbarBuilder.logo != null) {
                val logoSize = context.dpToIntPx(40)
                val logoMargin = context.dpToIntPx(16)
                val logoPlaceholder = getDrawable(context, R.drawable.logo_placeholder)

                logo = logoPlaceholder
                val logo = children.last() as? ImageView

                if (logo != null) {
                    logo.scaleType = ImageView.ScaleType.CENTER_CROP

                    (logo.layoutParams as? Toolbar.LayoutParams)?.let {
                        it.width = logoSize
                        it.height = logoSize
                        it.marginEnd = logoMargin
                        logo.layoutParams = it
                    }

                    Glide.with(context)
                        .load(this@ToolbarBuilder.logo)
                        .apply(circleCropTransform())
                        .override(logoSize)
                        .into(logo)
                }
            } else {
                logo = null
            }
        }
    }
}

data class MenuItemHolder(
    val title: String,
    val menuId: Int,
    val icon: Int,
    val actionViewLayout: Int?,
    val clickListener: ((MenuItem) -> Unit)? = null
)

class BottombarBuilder() {
    private var visible = true
    private val views = mutableListOf<Int>()
    private val tempViews = mutableListOf<Int>()

    fun addView(layoutId: Int) = apply { views.add(layoutId) }
    fun setVisibility(isVisible: Boolean) = apply { this.visible = isVisible }

    fun prepare(prepareFn: (BottombarBuilder.() -> Unit)?) = apply { prepareFn?.invoke(this) }

    fun invalidate() = apply {
        visible = true
        views.clear()
    }

    fun build(context: FragmentActivity) {

        if (tempViews.isNotEmpty()) {
            tempViews.forEach {
//                context.container.removeViewAt(it)
                val view = context.container.findViewById<View>(it)
                context.container.removeView(view)
            }
            tempViews.clear()
        }

        if (views.isNotEmpty()) {
            val inflater = LayoutInflater.from(context)
            views.forEach {
                val view = inflater.inflate(it, context.container, false)
                context.container.addView(view)
                tempViews.add(view.id)
            }
        }

        context.nav_view?.run {
            isVisible = visible
            ((layoutParams as CoordinatorLayout.LayoutParams)
                .behavior as HideBottomViewOnScrollBehavior).slideUp(this)
        }
    }


}