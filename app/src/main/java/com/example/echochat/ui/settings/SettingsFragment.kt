package com.example.echochat.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.echochat.MainActivity
import com.example.echochat.R
import com.example.echochat.databinding.FragmentSettingsBinding
import com.example.echochat.model.User
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.LocaleHelper
import com.example.echochat.util.USER_SESSION
import com.example.echochat.util.UiState
import com.example.echochat.util.hide
import com.example.echochat.util.show
import com.example.echochat.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.user = SharedPreferencesReManager.getData(USER_SESSION, User::class.java)
        binding.imgUpdateProfile.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_main)
                .navigate(R.id.action_homeFragment_to_updateProfileFragment)
        }

        binding.notificationLayout.setOnClickListener {
            viewModel.openAppNotificationSettings(requireContext())
        }

        binding.tvLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.changeLanguageLayout.setOnClickListener {
            showPopupMenu(it)
        }

        observer()

    }

    private fun observer() {
        viewModel.logout.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.progressBarLoadNotification.show()
                    binding.blockingView.show()
                }

                is UiState.Success-> {
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast(state.data)
                    logoutAction()
                }

                is UiState.Failure -> {
                    binding.progressBarLoadNotification.hide()
                    binding.blockingView.hide()
                    toast(state.error.toString())
                    logoutAction()
                }

                else -> {}
            }

        }
    }

    private fun logoutAction() {
        requireActivity().finish()
        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    private fun showPopupMenu(view: View){
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.gravity = Gravity.END
        popupMenu.menuInflater.inflate(R.menu.language_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.vietnamese -> {
                    LocaleHelper.setLocale("vi")
                    val intent = requireActivity().intent
                    requireActivity().finish()
                    requireActivity().startActivity(intent)
                }
                else -> {
                    LocaleHelper.setLocale("en")
                    val intent = requireActivity().intent
                    requireActivity().finish()
                    requireActivity().startActivity(intent)
                }
            }
            true
        }
        popupMenu.show()
    }

}