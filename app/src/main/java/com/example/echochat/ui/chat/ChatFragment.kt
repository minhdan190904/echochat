package com.example.echochat.ui.chat

import android.annotation.SuppressLint
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
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.echochat.R
import com.example.echochat.databinding.FragmentChatBinding
import com.example.echochat.model.Message
import com.example.echochat.model.User
import com.example.echochat.model.dto.MessageDTO
import com.example.echochat.network.NetworkMonitor
import com.example.echochat.util.CHAT_ID
import com.example.echochat.util.CHAT_REQUEST
import com.example.echochat.util.CHECK
import com.example.echochat.util.NORMAL_CLOSURE_STATUS
import com.example.echochat.util.RETRY_TIME_WEB_SOCKET
import com.example.echochat.util.UiState
import com.example.echochat.util.equalsTime
import com.example.echochat.util.formatOnlyDate
import com.example.echochat.util.myFriend
import com.example.echochat.util.myUser
import com.example.echochat.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
    private lateinit var chatAdapter: ChatAdapter
    private val chatTempList = mutableListOf<Message>()
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private var isNotInit: Boolean = false
    private var isWebSocketConnected: Boolean = false
    private var isWebSocketConnecting: Boolean = false
    private var isReadyChat: Boolean = false

    private var imageUri: Uri? = null
    private lateinit var dialog: BottomSheetDialog

    //bottom sheet
    private lateinit var responseAdapter: ListItemAllOptionGenerate
    private lateinit var textViewNoData: TextView
    private lateinit var textViewLoading: TextView
    private lateinit var textViewOptions: TextView
    private lateinit var linearProgressIndicator: LinearProgressIndicator
    private lateinit var recyclerViewResponse: RecyclerView
    private lateinit var dialogResponse: BottomSheetDialog

    @Inject
    lateinit var httpClient: OkHttpClient

    @Inject
    @Named(CHAT_REQUEST)
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
        isReadyChat = false
        observeNetworkAndConnect()
    }

    private fun connectWebSocket() {
        if (isWebSocketConnected || isWebSocketConnecting) return
        disconnectWebSocket()
        isWebSocketConnecting = true

        webSocket = httpClient.newWebSocket(requestChat, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                lifecycleScope.launch {
                    isWebSocketConnected = true
                    isWebSocketConnecting = false
                    toast("WebSocket connected")
                    viewModel.updateSeenLastMessage(CHAT_ID)
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                lifecycleScope.launch {
                    try {
                        val messageDTO = gson.fromJson(text, MessageDTO::class.java)
                        if (messageDTO.chatId == CHAT_ID && messageDTO.senderId != myUser?.id) {
                            val sender = viewModel.chat.value?.getOtherUser(myUser!!)
                            val message = Message(
                                id = messageDTO.id,
                                message = messageDTO.message,
                                sender = sender,
                                sendingTime = messageDTO.sendingTime ?: Date(),
                                isSeen = messageDTO.isSeen,
                                messageType = Message.MessageType.valueOf(
                                    messageDTO.messageType ?: Message.MessageType.TEXT.name
                                )
                            )
                            Log.i("ChatFragment", "Received WebSocket message: $message")
                            chatTempList.add(message)
                            chatTempList.sortBy { it.sendingTime }
                            chatAdapter.submitList(chatTempList)
                            binding.messagesRecyclerView.post {
                                binding.messagesRecyclerView.scrollToPosition(chatTempList.size - 1)
                            }
                            viewModel.updateSeenLastMessage(CHAT_ID)
                        }
                    } catch (e: Exception) {
                        Log.e("ChatFragment", "Error parsing WebSocket message: $text")
                    }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                lifecycleScope.launch {
                    isWebSocketConnected = false
                    isWebSocketConnecting = false
                    Log.i("ChatFragment", "WebSocket closed: $reason")
                    if (networkMonitor.isNetworkConnected() && code != NORMAL_CLOSURE_STATUS) {
                        connectWebSocket()
                    } else {
                        Log.e("ChatFragment", "Network is not available, cannot reconnect WebSocket")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                lifecycleScope.launch {
                    isWebSocketConnected = false
                    isWebSocketConnecting = false
                    toast("WebSocket error: ${t.message}")
                    if(networkMonitor.isNetworkConnected()) {
                        delay(RETRY_TIME_WEB_SOCKET)
                        if(isActive){
                            connectWebSocket()
                        }
                    } else {
                        Log.e("ChatFragment", "Network is not available, cannot reconnect WebSocket")
                    }
                }
            }
        })
    }

    private fun observeNetworkAndConnect() {
        networkMonitor.isNetworkAvailable.observe(viewLifecycleOwner) { isAvailable ->
            if (isAvailable) {
                if (!isWebSocketConnected) {
                    connectWebSocket()
                }
                if (!isReadyChat) {
                    isReadyChat = true
                    viewModel.getChat(CHAT_ID, false)
                }
            } else {
                if (!isReadyChat) {
                    isReadyChat = true
                    viewModel.getChat(CHAT_ID, false)
                }
                disconnectWebSocket()
            }
        }
    }

    private fun disconnectWebSocket() {
        webSocket?.close(1000, "Manually closed")
        webSocket = null
        isWebSocketConnected = false
        isWebSocketConnecting = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disconnectWebSocket()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initView()
        initBottomSheetResponse()
        observeValues()
        setClicks()
        binding.buttonTakePhoto.setOnClickListener {
            showBottomSheet()
        }

        if (myFriend != null) {
            getReadyVideoCall(myFriend!!)
            getReadyAudioCall(myFriend!!)
        }
    }

    private fun initBottomSheetResponse() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_list_response, null)
        dialogResponse = BottomSheetDialog(requireContext())
        dialogResponse.setContentView(dialogView)
        recyclerViewResponse = dialogView.findViewById(R.id.recycleViewResponse)
        linearProgressIndicator = dialogView.findViewById(R.id.progressIndicator)
        textViewNoData = dialogView.findViewById(R.id.tv_no_data)
        textViewLoading = dialogView.findViewById(R.id.textViewGenerating)
        textViewOptions = dialogView.findViewById(R.id.textViewOptions)
    }

    private fun getReadyVideoCall(targetUser: User) {
        binding.toolbar.iconVideoCall.apply {
            setIsVideoCall(true)
            setResourceID("zego_uikit_call")
            setInvitees(
                listOf(
                    ZegoUIKitUser(
                        targetUser.id.toString(),
                        targetUser.name
                    )
                )
            )
            setBackgroundResource(R.drawable.ic_webcam)
        }
    }

    private fun getReadyAudioCall(targetUser: User) {
        binding.toolbar.iconAudioCall.apply {
            setIsVideoCall(false)
            setResourceID("zego_uikit_call")
            setInvitees(
                listOf(
                    ZegoUIKitUser(
                        targetUser.id.toString(),
                        targetUser.name
                    )
                )
            )
            setBackgroundResource(R.drawable.ic_call)
        }
    }

    private fun initView() {

        chatAdapter = ChatAdapter(viewModel)

        binding.messagesRecyclerView.apply {
            setHasFixedSize(true)
            adapter = chatAdapter
        }

    }

    private fun eventClickChooseResponse(message: String) {
        binding.editTextMessage.setText(message)
        dialogResponse.dismiss()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeValues() {

        viewModel.responseUiState.observe(viewLifecycleOwner) { responseState ->
            when (responseState) {
                is UiState.Loading -> {
                    showBottomSheetResponses()
                    linearProgressIndicator.isVisible = true
                    recyclerViewResponse.isVisible = false
                    textViewNoData.isVisible = false
                    textViewLoading.isVisible = true
                    textViewOptions.isVisible = false

                }

                is UiState.NoData -> {
                    recyclerViewResponse.isVisible = false
                    textViewNoData.isVisible = true
                    linearProgressIndicator.isVisible = false
                    textViewLoading.isVisible = false
                }

                else -> {
                    linearProgressIndicator.isVisible = false
                    recyclerViewResponse.isVisible = true
                    textViewNoData.isVisible = false
                    textViewLoading.isVisible = false
                    textViewOptions.isVisible = true
                    responseAdapter = ListItemAllOptionGenerate(
                        viewModel.generateResponseAI.value ?: emptyList(),
                        ::eventClickChooseResponse
                    )
                    recyclerViewResponse.adapter = responseAdapter
                }
            }
        }

        viewModel.fileUrl.observe(viewLifecycleOwner) { fileUrl ->
            if (fileUrl != null) {
                SlidingImageDialogFragment
                    .newInstance(chatId = CHAT_ID, imageUrl = fileUrl)
                    .show(parentFragmentManager, "SlidingImageDialog")
            }
        }

        viewModel.messageUpdateSentForNotInternet.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                val receiver = myUser?.let { viewModel.chat.value?.getOtherUser(it) }
                val jsonMessage = gson.toJson(
                    MessageDTO(
                        id = message.id,
                        message = message.message,
                        senderId = message.sender?.id,
                        receiverId = receiver?.id,
                        chatId = CHAT_ID,
                        sendingTime = message.sendingTime,
                        messageType = message.messageType.name,
                        isSeen = message.isSeen
                    )
                )
                webSocket?.send(jsonMessage)
                for (existingMessage in chatTempList) {
                    if (existingMessage.sendingTime!!.equalsTime(message.sendingTime!!)) {
                        existingMessage.isUploading = false
                        break
                    }
                }
                chatTempList.sortBy { it.sendingTime }
                chatAdapter.submitList(chatTempList)
                chatAdapter.notifyDataSetChanged()
            }
        }

        viewModel.messageUpdateSentForInternet.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                for (existingMessage in chatTempList) {
                    if (existingMessage.sendingTime!!.equalsTime(message.sendingTime!!)) {
                        existingMessage.isUploading = false
                        break
                    }
                }
                chatTempList.sortBy { it.sendingTime }
                chatAdapter.submitList(chatTempList)
                chatAdapter.notifyDataSetChanged()
            }
        }

        viewModel.chat.observe(viewLifecycleOwner) { chat ->
            chatTempList.clear()
            chatTempList.addAll(chat.messageList)
            chatTempList.sortBy { it.sendingTime }
            chatAdapter.submitList(chatTempList)
            if (chatTempList.isNotEmpty()) {
                binding.messagesRecyclerView.post {
                    binding.messagesRecyclerView.scrollToPosition(chatTempList.size - 1)
                }
            }

            val receiver = if (chat.user1.id == myUser?.id) chat.user2 else chat.user1

            isNotInit = true

            initToolbar(receiver)
        }

        viewModel.messageData.observe(viewLifecycleOwner) { message ->

            if (message.messageType == Message.MessageType.IMAGE || message.messageType == Message.MessageType.VIDEO) {
                val existingMessageIndex = chatTempList.indexOfLast { existingMsg ->
                    existingMsg.messageType == message.messageType && existingMsg.isUploading
                }

                if (existingMessageIndex >= 0) {
                    chatTempList[existingMessageIndex] = message
                } else {
                    chatTempList.add(message)
                }
            } else {
                chatTempList.add(message)
            }
            chatTempList.sortBy { it.sendingTime }
            chatAdapter.submitList(chatTempList)
            if (chatTempList.isNotEmpty()) {
                binding.messagesRecyclerView.post {
                    binding.messagesRecyclerView.scrollToPosition(chatTempList.size - 1)
                }
            }

            val receiver = myUser?.let { viewModel.chat.value?.getOtherUser(it) }

            if (isNotInit) {
                if (message != null) {
                    val jsonMessage = gson.toJson(
                        MessageDTO(
                            id = message.id,
                            message = message.message,
                            senderId = message.sender?.id,
                            receiverId = receiver?.id,
                            chatId = CHAT_ID,
                            sendingTime = message.sendingTime,
                            messageType = message.messageType.name,
                            isSeen = message.isSeen
                        )
                    )
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

        viewModel.uploadingMessages.observe(viewLifecycleOwner) {
            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun showBottomSheetResponses() {
        dialogResponse.show()
    }

    private val resultContract =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let {
                uploadImageFromUri(it)
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let {
                    uploadImageFromUri(it)
                }
            }
        }

    @SuppressLint("QueryPermissionsNeeded")
    private fun captureImage() {
        val imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(
            this.requireContext(),
            "${requireContext().packageName}.provider",
            imageFile
        )

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

    private fun showBottomSheet() {
        val dialogView =
            layoutInflater.inflate(R.layout.bottom_sheet_dialog_choose_image_option, null)
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
                return
            }
            val mimeType = context?.contentResolver?.getType(uri) ?: return
            val mediaType = mimeType.toMediaTypeOrNull()
            val requestFile = file.asRequestBody(mediaType)
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            if (mimeType.contains("image")) {
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
        binding.toolbar.tvUserLastSeen.text =
            if (receiver.isOnline) getString(R.string.text_active_now)
            else getString(R.string.last_seen_time, lastSeenTime?.customLastSeenChat())
    }

    private fun Date.customLastSeenChat(): String {
        return when (val duration = Date().time - this.time) {
            in 0..<60000 -> getString(R.string.few_seconds_ago)
            in 60000..<3600000 -> getString(R.string.minutes_ago, duration / 60000)
            in 3600000..<86400000 -> getString(R.string.hours_ago, duration / 3600000)
            else -> this.formatOnlyDate()
        }
    }
}
