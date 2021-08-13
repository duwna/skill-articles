package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.remote.RestService
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val prefs: PrefManager,
    private val network: RestService
) : IRepository {

    fun getProfile(): LiveData<User?> = prefs.profileLive

    suspend fun uploadAvatar(body: MultipartBody.Part) {
        val (url) = network.upload(body, prefs.accessToken)
        prefs.replaceAvatarUrl(url)
    }
}