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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentRegisterBinding
import com.example.echochat.model.dto.LoginDTO
import com.example.echochat.util.PRIVACY_URL
import com.example.echochat.util.TERMS_URL
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.intentUrl
import com.example.echochat.util.show
import com.example.echochat.util.toast
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by activityViewModels()

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

            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

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

            etPassword.addTextChangedListener {
                val password = getPassword()
                when {
                    password.isEmpty() -> {
                        binding.password.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etPassword.error = getString(R.string.password_must_not_be_empty)
                    }

                    password.length < 6 -> {
                        binding.password.endIconMode = TextInputLayout.END_ICON_NONE
                        binding.etPassword.error = getString(R.string.password_valid_condition)
                    }

                    else -> {
                        binding.etPassword.error = null
                        binding.password.endIconMode = TextInputLayout.END_ICON_CUSTOM
                    }
                }
                checkValidateInput()
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkValidateInput()
            }

            observer()

            setUpSpannableCheckBox()
            setUpSpannableSignInText()
        }
    }

    private fun setUpSpannableSignInText() {
        val fullText = Html.fromHtml(getString(R.string.had_a_account_sign_in), Html.FROM_HTML_MODE_LEGACY).toString()
        val clickableText = getString(R.string.sign_in)
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf(clickableText)
        val endIndex = startIndex + clickableText.length
        if (startIndex >= 0) {
            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        findNavController().popBackStack()
                    }
                },
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.btnSignIn.text = spannableString
        binding.btnSignIn.movementMethod = LinkMovementMethod.getInstance()
        binding.btnSignIn.highlightColor = Color.TRANSPARENT
    }

    private fun setUpSpannableCheckBox() {
        val fullText = Html.fromHtml(getString(R.string.terms_and_policy), Html.FROM_HTML_MODE_LEGACY).toString()
        val termsText = getString(R.string.terms)
        val privacyText = getString(R.string.privacy_policy)

        val spannableString = SpannableString(fullText)

        val termsStart = fullText.indexOf(termsText)
        val termsEnd = termsStart + termsText.length

        val privacyStart = fullText.indexOf(privacyText)
        val privacyEnd = privacyStart + privacyText.length

        if (termsStart >= 0) {
            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        intentUrl(TERMS_URL)
                    }
                },
                termsStart,
                termsEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        if (privacyStart >= 0) {
            spannableString.setSpan(
                object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        intentUrl(PRIVACY_URL)
                    }
                },
                privacyStart,
                privacyEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvTermsAndPolicy.text = spannableString
        binding.tvTermsAndPolicy.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTermsAndPolicy.highlightColor = Color.TRANSPARENT
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
        val isAgree = binding.checkBox.isChecked
        binding.btnConfirmSignUp.isEnabled = isEmailValid && isPasswordValid && isNameValid && isAgree
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBarLoadNotification.show()
                    binding.blockingView.show()
                }

                is UiState.Failure -> {
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast(state.error ?: getString(R.string.register_failed))
                }

                is UiState.Success -> {
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast(getString(R.string.register_successful))
                    findNavController().popBackStack()
                }

                else -> {}
            }
        }
    }
}
