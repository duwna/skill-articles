package ru.skillbranch.skillarticles.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.selectDestination
import ru.skillbranch.skillarticles.ui.base.BaseActivity
import ru.skillbranch.skillarticles.viewmodels.RootViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

class RootActivity : BaseActivity<RootViewModel>() {


    override val layout: Int = R.layout.activity_root
    public override val viewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appBarConFiguration = AppBarConfiguration(
            setOf(
                R.id.nav_articles,
                R.id.nav_bookmarks,
                R.id.nav_transcriptions,
                R.id.nav_profile
            )
        )


        setupActionBarWithNavController(navController, appBarConFiguration)
//        nav_view.setupWithNavController(navController)

        nav_view.setOnNavigationItemSelectedListener {
            viewModel.navigate(NavigationCommand.To(it.itemId))
            true
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
             nav_view.selectDestination(destination)

        }
    }

//    override fun setupViews() {
//        setupToolbar()
//        setupBottomBar()
//        setupSubmenu()
//    }
//
//    override fun showSearchBar() {
//        bottombar.setSearchState(true)
//        scroll.setMarginOptionally(bottom = dpToIntPx(56))
//    }
//
//    override fun hideSearchBar() {
//        bottombar.setSearchState(false)
//        scroll.setMarginOptionally(bottom = dpToIntPx(0))
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_search, menu)
//        val searchItem = menu?.findItem(R.id.action_search)
//        val searchView = searchItem?.actionView as? SearchView
//        searchView?.queryHint = getString(R.string.article_search_placeholder)
//
//        if (binding.isSearch) {
//            searchItem?.expandActionView()
//            searchView?.setQuery(binding.searchQuery, false)
//            if (binding.isFocusedSearch) searchView?.requestFocus()
//            else searchView?.clearFocus()
//        }
//
//        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                viewModel.handleSearch(query)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                viewModel.handleSearch(newText)
//                return true
//            }
//        })
//
//        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
//            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
//                viewModel.handleSearchMode(true)
//                return true
//            }
//
//            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
//                viewModel.handleSearchMode(false)
//                return true
//            }
//        })
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun renderNotification(notify: Notify) {
        val snackbar = Snackbar.make(container, notify.message, Snackbar.LENGTH_LONG)

        if (bottombar != null) snackbar.anchorView = bottombar
        else snackbar.anchorView = nav_view

        when (notify) {
            is Notify.TextMessage -> {
            }
            is Notify.ActionMessage -> {
                with(snackbar) {
                    setActionTextColor(getColor(R.color.color_accent_dark))
                    setAction(notify.actionLabel) { notify.actionHandler.invoke() }
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) { notify.errHandler?.invoke() }
                }
            }
        }
        snackbar.show()
    }

    override fun subscribeOnState(state: IViewModelState) {
        // do something with state
    }


}
