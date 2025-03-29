package com.example.echochat.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentLoginBinding
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast
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

        
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        with(binding) {
            btnConfirmSignIn.setOnClickListener {
                if (validation()) {
                    viewModel.login(
                        email = etEmailLogin.text.toString(),
                        password =  etPasswordLogin.text.toString()
                    )
                }
            }
        }
        observer()
    }

    private fun validation(): Boolean {
        return true
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnConfirmSignIn.setText("")
                    binding.progressBarLoadNotification.show()
                }
                is UiState.Failure -> {
                    binding.btnConfirmSignIn.setText("Login")
                    binding.progressBarLoadNotification.hide()
                    toast(state.error ?: "Lỗi đăng nhập")
                }
                is UiState.Success -> {
                    binding.btnConfirmSignIn.setText("Login")
                    binding.progressBarLoadNotification.hide()
                    toast(state.data.toString())
                    findNavController().navigate(
                        R.id.action_loginFragment_to_homeFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.main_navigation, true)
//                            .setPopUpTo(R.id.registerFragment, true)
                            .build()
                    )
                }

                UiState.HasData -> TODO()
                UiState.NoData -> TODO()
            }
        }
    }
}