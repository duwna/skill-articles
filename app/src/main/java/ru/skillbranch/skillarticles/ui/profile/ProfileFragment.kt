package ru.skillbranch.skillarticles.ui.profile

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.ui.base.Binding
import ru.skillbranch.skillarticles.ui.delegates.RenderProp
import ru.skillbranch.skillarticles.ui.dialogs.AvatarActionDialog
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.profile.PendingAction
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileState
import ru.skillbranch.skillarticles.viewmodels.profile.ProfileViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by activityViewModels()
    override val layout: Int = R.layout.fragment_profile
    override val binding: ProfileBinding by lazy { ProfileBinding() }

    private val permissionResultCallback =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val permissionsResult = result.mapValues { (permission, isGranted) ->
                if (isGranted) true to true
                else false to ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), permission
                )
            }
            viewModel.handlePermission(permissionsResult)
        }

    private val galleryResultCallback =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            if (result != null) {
                val inputStream = requireContext().contentResolver.openInputStream(result)
                viewModel.handleUploadPhoto(inputStream)
            }

        }

    private val settingsResultCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

    private val cameraResultCallback =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
            val (payload) = binding.pendingAction as PendingAction.CameraAction
            if (result) {
                val inputStream = requireContext().contentResolver.openInputStream(payload)
                viewModel.handleUploadPhoto(inputStream)
            } else {
                removeTempUri(payload)
            }
        }

    private val editPhotoResultCallback = registerForActivityResult(EditImageContract()) { result ->
        if (result != null) {
            val inputStream = requireContext().contentResolver.openInputStream(result)
            viewModel.handleUploadPhoto(inputStream)
        } else {
            val (payload) = binding.pendingAction as PendingAction.EditAction
            removeTempUri(payload.second)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(AvatarActionDialog.AVATAR_ACTIONS_KEY) { _, bundle ->
            when (bundle[AvatarActionDialog.SELECT_ACTION_KEY] as String) {
                AvatarActionDialog.CAMERA_KEY -> viewModel.handleCameraAction(prepareTempUri())
                AvatarActionDialog.GALLERY_KEY -> viewModel.handleGalleryAction()
                AvatarActionDialog.DELETE_KEY -> viewModel.handleDeleteAction()
                AvatarActionDialog.EDIT_KEY -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val sourceFile =
                            Glide.with(requireActivity()).asFile().load(binding.avatar).submit()
                                .get()

                        val sourceUri = FileProvider.getUriForFile(
                            requireContext(),
                            "${requireContext().packageName}.provider",
                            sourceFile
                        )

                        withContext(Dispatchers.Main) {
                            viewModel.handleEditAction(sourceUri, prepareTempUri())
                        }
                    }
                }
            }
        }
    }

    override fun setupViews() {
        iv_avatar.setOnClickListener {
            val action = ProfileFragmentDirections
                .actionNavProfileToDialogAvatarActions(binding.avatar.isNotBlank())
            viewModel.navigate(NavigationCommand.To(action.actionId, action.arguments))
        }
        viewModel.observePermissions(viewLifecycleOwner) {
            permissionResultCallback.launch(it.toTypedArray())
        }
        viewModel.observeActivityResults(viewLifecycleOwner) { action ->
            when (action) {
                is PendingAction.GalleryAction -> galleryResultCallback.launch(action.payload)
                is PendingAction.SettingsAction -> settingsResultCallback.launch(action.payload)
                is PendingAction.CameraAction -> cameraResultCallback.launch(action.payload)
                is PendingAction.EditAction -> editPhotoResultCallback.launch(action.payload)
            }
        }
    }

    private fun prepareTempUri(): Uri {
        val timestamp = SimpleDateFormat("HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tempFile = File.createTempFile(
            "JPEG_$timestamp",
            ".jpg",
            storageDir
        )

        // must return contentUri, not fileUri
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )
    }

    private fun updateAvatar(avatarUrl: String) {
        if (avatarUrl.isBlank()) {
            Glide.with(this)
                .load(R.drawable.ic_avatar)
                .into(iv_avatar)
        } else {
            Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.ic_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_avatar)
        }
    }

    private fun removeTempUri(uri: Uri) {
        requireContext().contentResolver.delete(uri, null, null)
    }


    inner class ProfileBinding : Binding() {

        var pendingAction: PendingAction? = null

        var avatar by RenderProp("") { updateAvatar(it) }
        var name by RenderProp("") { tv_name.text = it }
        var about by RenderProp("") { tv_about.text = it }
        var rating by RenderProp(0) { tv_rating.text = "Rating $it" }
        var respect by RenderProp(0) { tv_respect.text = "Respect $it" }

        override fun bind(data: IViewModelState) {
            data as ProfileState

            if (data.avatar != null) avatar = data.avatar
            if (data.name != null) name = data.name
            if (data.about != null) about = data.about
            rating = data.rating
            respect = data.respect
            pendingAction = data.pendingAction
        }
    }
}