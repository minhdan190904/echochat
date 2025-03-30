package com.example.echochat.ui.auth

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.echochat.databinding.FragmentRegisterBinding
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnConfirmSignUp.setOnClickListener {
                viewModel.register(
                    email = getEmail(),
                    password = getPassword(),
                    name = getName()
                )
            }

            btnBackToLogin.setOnClickListener {
                findNavController().popBackStack()
            }

            etEmail.addTextChangedListener {
                val email = getEmail()
                when {
                    email.isEmpty() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmail.error = "Email không được để trống"
                    }

                    !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmail.error = "Email không hợp lệ"
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
                        binding.etFullName.error = "Tên không được để trống"
                    }

                    else -> {
                        binding.etFullName.error = null
                        binding.name.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    }
                }
                checkValidateInput()
            }

            etPassword.addTextChangedListener {
                val password = getPassword()
                when {
                    password.isEmpty() -> {
                        binding.password.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etPassword.error = "Mật khẩu không được để trống"
                    }

                    password.length < 6 -> {
                        binding.password.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
                    }

                    else -> {
                        binding.etPassword.error = null
                        binding.password.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    }
                }
                checkValidateInput()
            }
            observer()
        }
    }

    private fun getName(): String {
        return binding.etFullName.text.toString().trim()
    }

    private fun getEmail(): String {
        return binding.etEmail.text.toString().trim()
    }

    private fun getPassword(): String {
        return binding.etPassword.text.toString().trim()
    }

    private fun checkValidateInput() {
        val email = getEmail()
        val password = getPassword()
        val name = getName()
        val isEmailValid = binding.etEmail.error == null && email.isNotEmpty()
        val isPasswordValid = binding.etPassword.error == null && password.isNotEmpty()
        val isNameValid = binding.etFullName.error == null && name.isNotEmpty()
        binding.btnConfirmSignUp.isEnabled = isEmailValid && isPasswordValid && isNameValid
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnConfirmSignUp.text = ""
                    binding.progressBarLoadNotification.show()
                    binding.blockingView.show()
                }

                is UiState.Failure -> {
                    binding.btnConfirmSignUp.text = "Đăng ký"
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast(state.error ?: "Lỗi đăng ký")
                }

                is UiState.Success -> {
                    binding.btnConfirmSignUp.text = "Đăng ký"
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast("Đăng ký thành công")
                    findNavController().popBackStack()
                }

                UiState.HasData -> TODO()
                UiState.NoData -> TODO()
            }
        }
    }
}
