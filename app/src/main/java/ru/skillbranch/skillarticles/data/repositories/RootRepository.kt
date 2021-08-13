package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.LoginReq
import javax.inject.Inject

class RootRepository @Inject constructor(
    private val prefManager: PrefManager,
    private val network: RestService
) : IRepository {

    fun isAuth(): LiveData<Boolean> = prefManager.isAuthLive

    fun setAuth(auth: Boolean) {

    }

    suspend fun login(login: String, pass: String) {
        val auth = network.login(LoginReq(login, pass))
        prefManager.profile = auth.user
        prefManager.accessToken = "Bearer ${auth.accessToken}"
        prefManager.refreshToken = auth.refreshToken
    }
}