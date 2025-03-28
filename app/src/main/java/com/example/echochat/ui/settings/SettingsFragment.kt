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
import androidx.navigation.findNavController
import com.example.echochat.MainActivity
import com.example.echochat.R
import com.example.echochat.databinding.FragmentSettingsBinding
import com.example.echochat.model.User
import com.example.echochat.util.SharedPreferencesReManager
import com.example.echochat.util.LocaleHelper
import com.example.echochat.util.MY_USER_ID
import com.example.echochat.util.USER_SESSION
import com.google.android.material.bottomsheet.BottomSheetDialog


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
            SharedPreferencesReManager.clearAllData()
            MY_USER_ID = -1
//            requireActivity().findNavController(R.id.nav_host_fragment_main)
//                .navigate(
//                R.id.action_homeFragment_to_loginFragment,
//                null,
//                NavOptions.Builder()
//                    .setPopUpTo(R.id.main_navigation, true)
//                    .build()
//            )
            requireActivity().finish()
            requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))

        }

        binding.changeLanguageLayout.setOnClickListener {
            showPopupMenu(it)
        }

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