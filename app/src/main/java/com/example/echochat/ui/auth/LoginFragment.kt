package com.example.echochat.ui.auth

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentLoginBinding
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession { user ->
            if (user != null) {
                findNavController().navigate(
                    R.id.action_loginFragment_to_homeFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.main_navigation, true)
                        .build()
                )
            } else {
                Log.e("SESSION_TAG", "User session is null")
            }
        }

        with(binding) {
            btnConfirmSignIn.setOnClickListener {
                viewModel.login(
                    email = getEmailLogin(),
                    password = getPasswordLogin()
                )
            }

            btnSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            binding.etEmailLogin.addTextChangedListener {
                val emailLogin = it.toString().trim()
                when {
                    emailLogin.isEmpty() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmailLogin.error = "Email không được để trống"
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmailLogin.error = "Email không hợp lệ"
                    }
                    else -> {
                        binding.etEmailLogin.error = null
                        binding.email.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    }
                }
                checkValidateInput()
            }

            binding.etPasswordLogin.addTextChangedListener {
                val passwordLogin = it.toString().trim()
                if (passwordLogin.isEmpty()) {
                    binding.password.endIconMode = TextInputLayout.END_ICON_NONE
                    binding.etPasswordLogin.error = "Password không được để trống"
                } else {
                    binding.etPasswordLogin.error = null
                    binding.password.endIconMode = TextInputLayout.END_ICON_CUSTOM
                }
                checkValidateInput()
            }


        }
        observer()
    }

    private fun checkValidateInput() {
        val email = getEmailLogin()
        val password = getPasswordLogin()
        val isEmailValid = binding.etEmailLogin.error == null && email.isNotEmpty()
        val isPasswordValid = binding.etPasswordLogin.error == null && password.isNotEmpty()
        binding.btnConfirmSignIn.isEnabled = isEmailValid && isPasswordValid
    }

    private fun getEmailLogin(): String {
        return binding.etEmailLogin.text.toString().trim()
    }

    private fun getPasswordLogin(): String {
        return binding.etPasswordLogin.text.toString().trim()
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.progressBarLoadNotification.show()
                    binding.blockingView.show()
                }
                is UiState.Failure -> {
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast(state.error ?: "Lỗi đăng nhập")
                }
                is UiState.Success -> {
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast("Đăng nhập thành công")
                    findNavController().navigate(
                        R.id.action_loginFragment_to_homeFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.main_navigation, true)
                            .build()
                    )
                }

                else -> {}
            }
        }
    }
}