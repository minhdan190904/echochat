package com.example.echochat.ui.auth

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
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
            }
        }

        with(binding) {
            btnConfirmSignIn.setOnClickListener {
                viewModel.login(
                    email = getEmailLogin(),
                    password = getPasswordLogin()
                )
            }

            binding.etEmailLogin.addTextChangedListener {
                val emailLogin = it.toString().trim()
                when {
                    emailLogin.isEmpty() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmailLogin.error = getString(R.string.email_must_not_be_empty)
                    }
                    !Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches() -> {
                        binding.email.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etEmailLogin.error = getString(R.string.email_is_not_valid)
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
                    binding.etPasswordLogin.error = getString(R.string.password_must_not_be_empty)
                } else {
                    binding.etPasswordLogin.error = null
                    binding.password.endIconMode = TextInputLayout.END_ICON_CUSTOM
                }
                checkValidateInput()
            }

        }

        observer()

        setUpSpannable()

    }

    private fun setUpSpannable() {
        val fullText = Html.fromHtml(getString(R.string.new_user_sign_up), Html.FROM_HTML_MODE_LEGACY).toString()
        val clickableText = getString(R.string.sign_up)
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(clickableText)
        val endIndex = startIndex + clickableText.length
        if (startIndex >= 0) {
            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                    }
                },
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.btnSignUp.text = spannableString
        binding.btnSignUp.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSignUp.highlightColor = Color.TRANSPARENT
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
                    toast(state.error ?: getString(R.string.login_failed))
                }
                is UiState.Success -> {
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast(getString(R.string.login_successful))
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