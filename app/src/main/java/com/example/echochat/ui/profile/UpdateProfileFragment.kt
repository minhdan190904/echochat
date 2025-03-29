package com.example.echochat.ui.profile

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentUpdateProfileBinding
import com.example.echochat.util.BindingUtils.setImageUrl
import com.example.echochat.util.UiState
import com.example.echochat.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    private lateinit var dialog: BottomSheetDialog
    private var imageUri: Uri? = null
    private val viewModel: UpdateProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvUpdateProfile.setOnClickListener {
            toast(viewModel.name.value +  " " + viewModel.email.value)
        }
        binding.btnChangeProfile.setOnClickListener {
            showBottomSheet()
        }

        binding.etBirthday.setOnClickListener {
            showDatePicker()
        }

        observeValues()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.etBirthday.setText(selectedDate)
        }, year, month, day)

        datePicker.show()
    }

    private fun observeValues() {
        viewModel.updateUiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is UiState.HasData -> {
                    binding.progressBar.visibility = View.GONE
                    findNavController().popBackStack()
                }

                is UiState.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    toast(uiState.error!!)
                }

                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        viewModel.updateAvatarUiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                UiState.HasData -> {
                    binding.progressBar.visibility = View.GONE
                    binding.imageProfile.setImageUrl(viewModel.profileImageUrl.value)
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private val resultContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Log.i("MYTAG", "URI: $it")
            uploadImageFromUri(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let {
                uploadImageFromUri(it)
            }
        }
    }

    private fun captureImage() {
        val imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(this.requireContext(), "${requireContext().packageName}.provider", imageFile)

        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        if (callCameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(callCameraIntent)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir("Pictures")
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun uploadImageFromUri(uri: Uri) {
        val filePath = getFileFromUri(this.requireContext(), uri)?.absolutePath
        filePath?.let { path ->
            val file = File(path)
            if (!file.exists()) {
                Log.e("MYTAG", "File không tồn tại: $path")
                return
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            viewModel.uploadAvatarImage(imagePart)
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val file = File(context.cacheDir, "image.jpg")
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }

    private fun showBottomSheet(){
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_choose_image_option, null)
        dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(dialogView)
        dialogView.findViewById<View>(R.id.tvChooseImageGallery).setOnClickListener {
            resultContract.launch("image/*")
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.tvChooseImageCamera).setOnClickListener {
            captureImage()
            dialog.dismiss()
        }
        dialog.show()
    }

}