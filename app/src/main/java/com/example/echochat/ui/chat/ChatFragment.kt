package com.example.echochat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.echochat.R
import com.example.echochat.databinding.FragmentChatBinding
import com.example.echochat.model.Message
import com.example.echochat.model.User
import com.example.echochat.model.dto.MessageDTO
import com.example.echochat.network.NetworkMonitor
import com.example.echochat.ui.conversations.ConversationViewModel
import com.example.echochat.util.CHAT_ID
import com.example.echochat.util.CHECK
import com.example.echochat.util.customLastSeenChat
import com.example.echochat.util.myUser
import com.example.echochat.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private val chatAdapter = ChatAdapter()
    private val chatTempList = mutableListOf<Message>()
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private var isNotInit: Boolean = false
    private var isWebSocketConnected: Boolean = false

    @Inject
    lateinit var httpClient: OkHttpClient

    @Inject
    @Named("chat")
    lateinit var requestChat: Request

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        CHECK = false
        observeNetworkAndConnect()
    }

    private fun observeNetworkAndConnect() {
        networkMonitor.isNetworkAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                viewModel.getChat(CHAT_ID, CHECK)
                if (!isWebSocketConnected) {
                    connectWebSocket()
                }
            } else {
                CHECK = true
                disconnectWebSocket()
            }
        }
    }

    private fun disconnectWebSocket() {
        webSocket?.let {
            it.close(1000, "Network unavailable or fragment stopped")
            isWebSocketConnected = false
            webSocket = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectWebSocket()
        Log.i("MYTAG", "Fragment destroyed")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initView()
        observeValues()
        setClicks()
        binding.buttonTakePhoto.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun connectWebSocket() {
        webSocket = httpClient.newWebSocket(requestChat, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                lifecycleScope.launch {
                    viewModel.updateSeenLastMessage(CHAT_ID)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                lifecycleScope.launch {
                    try {
                        val messageDTO = gson.fromJson(text, MessageDTO::class.java)
                        if (messageDTO.chatId == CHAT_ID && messageDTO.message.sender?.id != myUser?.id) {
                            chatTempList.add(messageDTO.message)
                            chatAdapter.submitList(chatTempList)
                            chatAdapter.notifyDataSetChanged()
                            binding.messagesRecyclerView.post {
                                binding.messagesRecyclerView.scrollToPosition(chatTempList.size - 1)
                            }
                            viewModel.updateSeenLastMessage(CHAT_ID)
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocket", "JSON Parse Error: ${e.message}")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                lifecycleScope.launch {
                    toast(t.message.toString())
                    Log.i("WebSocket", "Error: ${t.message}")
                }
            }
        })
    }

    private fun initView() {
        with(binding) {
            messagesRecyclerView.setHasFixedSize(true)
            messagesRecyclerView.adapter = chatAdapter

            messagesRecyclerView.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
                if (bottom < oldBottom)
                    messagesRecyclerView.layoutManager?.smoothScrollToPosition(
                        messagesRecyclerView,
                        null,
                        chatAdapter.itemCount
                    )
            }
        }
    }

    private fun observeValues() {
        viewModel.chat.observe(viewLifecycleOwner) { chat ->
            chatAdapter.submitList(chat.messageList)
            chatTempList.clear()
            chatTempList.addAll(chat.messageList)
            chatAdapter.notifyDataSetChanged()
            if (chatTempList.isNotEmpty()) {
                binding.messagesRecyclerView.post {
                    binding.messagesRecyclerView.scrollToPosition(chatTempList.size - 1)
                }
            }

            val receiver = if (chat.user1.id == myUser?.id) chat.user2 else chat.user1

            if (isNotInit) {
                val lastMessage = chat.getLastMessage()
                if (lastMessage != null) {
                    val jsonMessage = gson.toJson(chat.id?.let { MessageDTO(lastMessage, receiver.id!!, CHAT_ID) })
                    webSocket?.send(jsonMessage)
                }
            }

            isNotInit = true

            initToolbar(receiver)
        }

        viewModel.messageData.observe(viewLifecycleOwner) { message->
            chatTempList.add(message)
            chatAdapter.submitList(chatTempList)
//            chatAdapter.notifyDataSetChanged()
            if (chatTempList.isNotEmpty()) {
                binding.messagesRecyclerView.post {
                    binding.messagesRecyclerView.scrollToPosition(chatTempList.size - 1)
                }
            }

            val receiver = myUser?.let { viewModel.chat.value?.getOtherUser(it) }

            if (isNotInit) {
                if (message != null) {
                    val jsonMessage = gson.toJson(MessageDTO(message, receiver?.id!!, CHAT_ID))
                    webSocket?.send(jsonMessage)
                }
            }
            isNotInit = true
        }

        viewModel.imageUrl.observe(viewLifecycleOwner) { url ->
            url?.let {
                viewModel.sendImageMessage()
            }
        }
        viewModel.videoUrl.observe(viewLifecycleOwner) { url ->
            url?.let {
                viewModel.sendVideoMessage()
            }
        }
    }


    private var imageUri: Uri? = null
    private lateinit var dialog: BottomSheetDialog

    private val resultContract = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            Log.i("MYTAG", "URI: $it")
            uploadImageFromUri(it)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri?.let {
                uploadImageFromUri(it)
            }
        }
    }

    private fun captureImage() {
        val imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(this.requireContext(), "${requireContext().packageName}.provider", imageFile)

        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        if (callCameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            cameraLauncher.launch(callCameraIntent)
        }
    }

    private fun getFileFromUri(context: Context, uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null

        val mineType = context.contentResolver.getType(uri)
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mineType) ?: "tmp"
        val fileName = "${System.currentTimeMillis()}.$extension"
        val file = File(context.cacheDir, fileName)
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return file
    }

    private fun showBottomSheet(){
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_dialog_choose_image_option, null)
        dialog = BottomSheetDialog(this.requireContext())
        dialog.setContentView(dialogView)
        dialogView.findViewById<View>(R.id.tvChooseImageGallery).setOnClickListener {
            resultContract.launch(arrayOf("image/*", "video/*"))
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.tvChooseImageCamera).setOnClickListener {
            captureImage()
            dialog.dismiss()
        }
        dialog.show()
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
            val mimeType = context?.contentResolver?.getType(uri) ?: return
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mediaType)
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            if(mimeType.contains("image")) {
                viewModel.uploadImage(filePart)
            } else {
                viewModel.uploadVideo(filePart)
            }
        }
    }

    private fun setClicks() {
        binding.toolbar.iconBack.setOnClickListener {
            findNavController().popBackStack()
            requireActivity().finish()
        }
    }

    private fun initToolbar(receiver: User) {
        binding.toolbar.tvUserLastSeen.isVisible = true
        val lastSeenTime = receiver.lastSeen
        Log.i("MYTAG", "Last seen: $lastSeenTime")
        binding.toolbar.tvUserLastSeen.text =
            if (receiver.isOnline) getString(R.string.text_active_now)
            else getString(R.string.last_seen_time, lastSeenTime?.customLastSeenChat())
    }
}
