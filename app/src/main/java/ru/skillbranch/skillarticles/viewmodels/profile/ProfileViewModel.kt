package ru.skillbranch.skillarticles.viewmodels.profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.skillbranch.skillarticles.data.repositories.ProfileRepository
import ru.skillbranch.skillarticles.viewmodels.base.*
import java.io.InputStream

class ProfileViewModel(handle: SavedStateHandle) :
    BaseViewModel<ProfileState>(handle, ProfileState()) {

    private val repository = ProfileRepository
    private val activityResults = MutableLiveData<Event<PendingAction>>()

    private val storagePermissions = listOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    init {
        subscribeOnDataSource(repository.getProfile()) { profile, state ->
            profile ?: return@subscribeOnDataSource null
            state.copy(
                name = profile.name,
                avatar = profile.avatar,
                about = profile.about,
                rating = profile.rating,
                respect = profile.respect
            )
        }
    }

    private fun startForResult(action: PendingAction) {
        activityResults.value = Event(action)
    }

    fun handleTestAction(source: Uri, destination: Uri) {
        val pendingAction = PendingAction.EditAction(source to destination)
        updateState { it.copy(pendingAction = pendingAction) }
        requestPermissions(storagePermissions)
    }

    fun handlePermission(permissionResult: Map<String, Pair<Boolean, Boolean>>) {
        val isAllGranted = !permissionResult.values.map { it.first }.contains(false)
        val isAllMayBeShown = !permissionResult.values.map { it.second }.contains(false)

        when {
            isAllGranted -> executePendingAction()
            !isAllMayBeShown -> executeOpenSettings()
            else -> {
                val msg = Notify.ErrorMessage(
                    "Need permission for storage",
                    "Retry"
                ) { requestPermissions(storagePermissions) }
                notify(msg)
            }
        }
    }

    private fun executeOpenSettings() {
        val handler = {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:ru.skillbranch.skillarticles")
            }
            startForResult(PendingAction.SettingsAction(intent))
        }
        notify(Notify.ErrorMessage("Need permissions for storage", "Open settings", handler))
    }

    private fun executePendingAction() {
        val pendingAction = currentState.pendingAction ?: return
        startForResult(pendingAction)
    }

    fun handleUploadPhoto(inputStream: InputStream?) {
        inputStream ?: return
        launchSafety(null, { updateState { it.copy(pendingAction = null) } }) {
            val byteArray = withContext(Dispatchers.IO) { inputStream.use { it.readBytes() } }
            val reqFile = byteArray.toRequestBody("image/jpeg".toMediaType())
            val body = MultipartBody.Part.createFormData("avatar", "name.jpg", reqFile)
            repository.uploadAvatar(body)
        }
    }

    fun observeActivityResults(owner: LifecycleOwner, handler: (PendingAction) -> Unit) {
        activityResults.observe(owner, EventObserver { handler(it) })
    }

    fun handleEditAction(source: Uri, destination: Uri) {
        updateState { it.copy(pendingAction = PendingAction.EditAction(source to destination)) }
        requestPermissions(storagePermissions)
    }

    fun handleDeleteAction() {

    }

    fun handleGalleryAction() {
        updateState { it.copy(pendingAction = PendingAction.GalleryAction("image/jpeg")) }
        requestPermissions(storagePermissions)
    }

    fun handleCameraAction(destination: Uri) {
        updateState { it.copy(pendingAction = PendingAction.CameraAction(destination)) }
        requestPermissions(storagePermissions)
    }
}

data class ProfileState(
    val avatar: String? = null,
    val name: String? = null,
    val about: String? = null,
    val rating: Int = 0,
    val respect: Int = 0,
    val pendingAction: PendingAction? = null
) : IViewModelState {

    override fun save(outState: SavedStateHandle) {
        outState.set("pendingAction", pendingAction)
    }

    override fun restore(savedState: SavedStateHandle): IViewModelState {
        return copy(pendingAction = savedState["pendingAction"])
    }
}

sealed class PendingAction : Parcelable {
    abstract val payload: Any?

    @Parcelize
    data class GalleryAction(override val payload: String) : PendingAction()

    @Parcelize
    data class SettingsAction(override val payload: Intent) : PendingAction()

    @Parcelize
    data class CameraAction(override val payload: Uri) : PendingAction()

    data class EditAction(override val payload: Pair<Uri, Uri>) : PendingAction(), Parcelable {
        constructor(parcel: Parcel)
                : this(Uri.parse(parcel.readString()) to Uri.parse(parcel.readString()))

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(payload.first.toString())
            dest.writeString(payload.second.toString())
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<EditAction> {
            override fun createFromParcel(parcel: Parcel): EditAction {
                return EditAction(parcel)
            }

            override fun newArray(size: Int): Array<EditAction?> {
                return arrayOfNulls(size)
            }
        }
    }
}