package com.example.echochat.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentLoginBinding
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        observer()
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
                    findNavController().navigate(R.id.action_loginFragment_to_navigation_graph_main)
                }

                UiState.HasData -> TODO()
                UiState.NoData -> TODO()
            }
        }
    }
}