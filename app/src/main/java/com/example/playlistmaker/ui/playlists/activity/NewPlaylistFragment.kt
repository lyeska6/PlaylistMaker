package com.example.playlistmaker.ui.playlists.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.ui.playlists.view_model.NewPlaylistViewModel
import com.example.playlistmaker.ui.playlists.view_model.NewPlaylistViewModel.Companion.DIR_NAME
import com.example.playlistmaker.ui.root.RootActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

open class NewPlaylistFragment: Fragment() {

    open val viewModel by viewModel<NewPlaylistViewModel>()

    lateinit var binding: FragmentNewPlaylistBinding
    private lateinit var inputMethodManager: InputMethodManager

    var isCoverLoaded: Uri? = null

    private lateinit var confirmDialog: MaterialAlertDialogBuilder
    private var deniedCounter = 0

    val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            goBack()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean->
        if (isGranted) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            deniedCounter = 0
        } else {
            if (deniedCounter < 2) {
                deniedCounter += 1
            } else {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data= Uri.fromParts("package", requireContext().packageName, null)
                requireContext().startActivity(intent)
            }
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Glide.with(requireContext())
                .load(uri)
                .transform(
                    CenterCrop(),
                    RoundedCorners(dpToPx(8F, requireContext())))
                .into(binding.playlistCoverView)
            isCoverLoaded = uri
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backCallback)

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.cancel_playlist_dialog_title)
            .setMessage(R.string.cancel_playlist_dialog_message)
            .setNeutralButton(R.string.cancel) { _, _ ->
            }
            .setPositiveButton(R.string.end) { _, _ ->
                reallyGoBack()
            }

        binding.playlistCoverView.setOnClickListener {
            val permissionProvided = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
            if (permissionProvided == PackageManager.PERMISSION_GRANTED) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else if (permissionProvided == PackageManager.PERMISSION_DENIED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        binding.nameEditText.doOnTextChanged { s, _, _, _ ->
            binding.nameEditTextHint.isVisible = !s.isNullOrEmpty()
            binding.createNewPlaylistBut.isEnabled = !s.isNullOrEmpty()
            if (s.isNullOrEmpty()) {
                binding.nameEditText.setBackgroundDrawable(requireContext().getDrawable(R.drawable.new_playlist_edittext))
            } else {
                binding.nameEditText.setBackgroundDrawable(requireContext().getDrawable(R.drawable.new_playlist_edittext_filled))
            }
        }

        binding.nameEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager.hideSoftInputFromWindow(binding.nameEditText.windowToken, 0)
            }
            false
        }

        binding.descriptionEditText.doOnTextChanged { s, _, _, _ ->
            binding.descriptionEditTextHint.isVisible = !s.isNullOrEmpty()
            if (s.isNullOrEmpty()) {
                binding.descriptionEditText.setBackgroundDrawable(requireContext().getDrawable(R.drawable.new_playlist_edittext))
            } else {
                binding.descriptionEditText.setBackgroundDrawable(requireContext().getDrawable(R.drawable.new_playlist_edittext_filled))
            }
        }

        binding.descriptionEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager.hideSoftInputFromWindow(binding.descriptionEditText.windowToken, 0)
            }
            false
        }

        binding.createNewPlaylistBut.setOnClickListener {
            savePlaylist()
        }

        binding.buttonBack.setOnClickListener {
            goBack()
        }
    }

    open fun savePlaylist() {
        var theFileName = ""
        if (isCoverLoaded != null) {
            val fileName = binding.nameEditText.text.toString()
            theFileName = saveImageToPrivateStorage(fileName, isCoverLoaded!!)
        }
        viewModel.createPlaylist(binding.nameEditText.text.toString(),
            binding.descriptionEditText.text.toString(),
            theFileName)
        Toast.makeText(requireContext(), "Плейлист ${binding.nameEditText.text} создан", Toast.LENGTH_LONG).show()
        reallyGoBack()
    }

    fun saveImageToPrivateStorage(fileName: String, uri: Uri): String {
        var file = File(requireContext().getDir(DIR_NAME, Context.MODE_PRIVATE), fileName)
        var newFileName = fileName
        var s = 0
        while (file.exists()) {
            s += 1
            Log.d("tag1", "file exists")
            newFileName = "$fileName $s"
            file = File(requireContext().getDir(DIR_NAME, Context.MODE_PRIVATE), newFileName)
        }
        Log.d("tag1", "in saving file exists is ${file.exists()}")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        Log.d("tag1", "after saving file exists is ${file.exists()}")
        return newFileName
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
        ).toInt()
    }

    open fun goBack() {
        if (binding.nameEditText.text.isNullOrEmpty() and binding.descriptionEditText.text.isNullOrEmpty() and (isCoverLoaded == null)) {
            reallyGoBack()
        } else {
            confirmDialog.show()
        }
    }

    open fun reallyGoBack() {
        backCallback.isEnabled = false
        if (requireActivity() is RootActivity) {
            (activity as RootActivity).showBottomNavigationView()
        }
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}