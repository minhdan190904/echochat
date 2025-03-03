package com.example.echochat.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentRegisterBinding
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast

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
        observer()
        with(binding) {
            btnConfirmSignUp.setOnClickListener {
                if (validation()) {
                    viewModel.register(
                        email = etEmail.text.toString(),
                        password =  etPassword.text.toString(),
                        name = etFullName.text.toString()
                    )
                }
            }
        }
    }

    private fun validation(): Boolean {
        return true
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnConfirmSignUp.setText("")
                    binding.progressBarLoadNotification.show()
                }
                is UiState.Failure -> {
                    binding.btnConfirmSignUp.setText("Register")
                    binding.progressBarLoadNotification.hide()
                    toast(state.error ?: "Lỗi đăng ký")
                }
                is UiState.Success -> {
                    binding.btnConfirmSignUp.setText("Register")
                    binding.progressBarLoadNotification.hide()
                    toast(state.data.toString())
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }

                UiState.HasData -> TODO()
                UiState.NoData -> TODO()
            }
        }
    }




}