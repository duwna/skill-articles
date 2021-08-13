package ru.skillbranch.skillarticles.viewmodels.auth

import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    handle: SavedStateHandle,
    private val repository: RootRepository
) : BaseViewModel<AuthState>(handle, AuthState()),
    IAuthViewModel {

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    override fun handleLogin(login: String, pass: String, dest: Int?) {
        launchSafety {
            repository.login(login, pass)
            navigate(NavigationCommand.FinishLogin(dest))
        }
    }
}

data class AuthState(val isAuth: Boolean = false) : IViewModelState
