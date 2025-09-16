package com.example.echochat.ui.profile

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentUpdateProfileBinding
import com.example.echochat.util.BindingUtils.setImageUrl
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
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

    private val requestCameraPerm =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCameraCapture()
            } else {
                requireContext().toast("Permission denied")
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && imageUri != null) {
                uploadImageFromUri(imageUri!!)
            } else {
                Log.i("UpdateProfile", "TakePicture canceled or failed")
            }
        }

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
            toast(viewModel.name.value + " " + viewModel.email.value)
        }
        binding.btnChangeProfile.setOnClickListener {
            showBottomSheet()
        }

        binding.etBirthday.setOnClickListener {
            showDatePicker()
        }

        with(binding) {
            etEmail.addTextChangedListener {
                val email = getEmail()
                when {
                    email.isEmpty() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmail.error = getString(R.string.email_must_not_be_empty)
                    }

                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmail.error = getString(R.string.email_is_not_valid)
                    }

                    else -> {
                        binding.etEmail.error = null
                        binding.email.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    }
                }
                checkValidateInput()
            }

            etFullName.addTextChangedListener {
                val name = getName()
                when {
                    name.isEmpty() -> {
                        binding.name.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etFullName.error = getString(R.string.name_must_not_be_empty)
                    }

                    else -> {
                        binding.etFullName.error = null
                        binding.name.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    }
                }
                checkValidateInput()
            }
        }

        observeValues()
    }

    private fun checkValidateInput() {
        val email = getEmail()
        val name = getName()
        val isEmailValid = binding.etEmail.error == null && email.isNotEmpty()
        val isNameValid = binding.etFullName.error == null && name.isNotEmpty()
        binding.tvUpdateProfile.isEnabled = isEmailValid && isNameValid
    }

    private fun getName(): String = binding.etFullName.text.toString().trim()

    private fun getEmail(): String = binding.etEmail.text.toString().trim()

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
                    binding.progressBar.show()
                    binding.blockingView.show()
                }

                is UiState.HasData -> {
                    binding.progressBar.hide()
                    binding.blockingView.hide()
                    findNavController().popBackStack()
                }

                is UiState.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    toast(uiState.error!!)
                }

                else -> {
                    binding.progressBar.hide()
                    binding.blockingView.hide()
                }
            }
        }

        viewModel.updateAvatarUiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                UiState.Loading -> {
                    binding.progressBar.show()
                    binding.blockingView.show()
                }
                UiState.HasData -> {
                    binding.progressBar.hide()
                    binding.blockingView.hide()
                    binding.imageProfile.setImageUrl(viewModel.profileImageUrl.value)
                }
                else -> {
                    binding.progressBar.hide()
                    binding.blockingView.hide()
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


    private fun captureImage() {
        val hasCam = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasCam) {
            startCameraCapture()
        } else {
            requestCameraPerm.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCameraCapture() {
        // Tạo file và uri qua FileProvider (authority: <package>.fileprovider)
        val file = createImageFile()
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )

        // Launch TakePicture
        takePictureLauncher.launch(imageUri)
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

    private fun showBottomSheet() {
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
