package com.example.echochat.repository

import android.util.Log
import com.example.echochat.model.Chat
import com.example.echochat.model.GroupChat
import com.example.echochat.model.Message
import com.example.echochat.model.NormalChat
import com.example.echochat.model.User
import com.example.echochat.network.NetworkResource
import com.example.echochat.util.Helper
import com.example.echochat.util.MY_USER_ID
import kotlinx.coroutines.delay
import kotlin.random.Random

class UserRepository {
    private val friendsList = mutableListOf<User>()
    private val chatList = mutableListOf<Chat>()
    private val sampleProfileImageUrlList = mutableListOf<String>()
    lateinit var myUser: User

    init {
        //Initialize mock data
        initSampleProfileImageUrls()
        initMyProfile()
        initFriends()
        initChatList()
    }


    suspend fun getMyFriendList(query: String?): NetworkResource<List<User>> {
        return if (query.isNullOrEmpty()) {
            if (query == null) delay(500L)
            NetworkResource.Success(friendsList)
        } else {
            NetworkResource.Success(friendsList.filter { it.name.contains(query, true) })
        }

    }

    suspend fun getMyConversations(query: String?): NetworkResource<List<Chat>> {
        return if (query.isNullOrEmpty()) {
            if (query == null) delay(500L)  //Delay first time only
            NetworkResource.Success(chatList)
        } else {
                NetworkResource.Success(chatList.filter { it.getChatTitle().contains(query, true) })
        }
    }

    suspend fun getChatById(chatId: Int): NetworkResource<Chat?> {
        delay(1000L)
        chatList.forEach { chat ->
            Log.i("MYTAG", "Chat id: ${chat.id}, title: ${chat.getChatTitle()}")
        }

        return NetworkResource.Success(chatList.find { it.id == 2 })
    }

    suspend fun sendMessage(chatId: Int, message: Message): NetworkResource<Chat?> {
        val chat = chatList.find { it.id == chatId }
        chat?.addMessage(message)
        return NetworkResource.Success(chat)
    }


    private fun initSampleProfileImageUrls() {
        //1th and 2nd one is group photo

        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1558898479-33c0057a5d12?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1607748851687-ba9a10438621?ixid=MXwxMjA3fDB8MHxzZWFyY2h8MTV8fGdyb3VwJTIwb2YlMjBmcmllbmRzfGVufDB8fDB8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1526560244950-1a3c1ace48f9?ixid=MXwxMjA3fDB8MHxzZWFyY2h8Mjh8fHByb2ZpbGUlMjBwaWN0dXJlfGVufDB8fDB8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")

        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1572316787289-4d87404eea4e?ixid=MXwxMjA3fDB8MHxzZWFyY2h8OXx8cHJvZmlsZSUyMHBpY3R1cmV8ZW58MHx8MHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1561055657-b9e0bf0fa360?ixid=MXwxMjA3fDB8MHxzZWFyY2h8MXx8cHJvZmlsZSUyMHBpY3R1cmV8ZW58MHx8MHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1598550874175-4d0ef436c909?ixid=MXwxMjA3fDB8MHxzZWFyY2h8Mnx8cHJvZmlsZSUyMHBpY3R1cmV8ZW58MHx8MHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1544435253-f0ead49638fa?ixid=MXwxMjA3fDB8MHxzZWFyY2h8M3x8cHJvZmlsZSUyMHBpY3R1cmV8ZW58MHx8MHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1605993439219-9d09d2020fa5?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=334&q=80")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1603415526960-f7e0328c63b1?ixid=MXwxMjA3fDB8MHxzZWFyY2h8MTN8fHByb2ZpbGUlMjBwaWN0dXJlfGVufDB8fDB8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")
        sampleProfileImageUrlList.add("https://images.unsplash.com/photo-1606513542745-97629752a13b?ixid=MXwxMjA3fDB8MHxzZWFyY2h8MTh8fHByb2ZpbGUlMjBwaWN0dXJlfGVufDB8fDB8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60")


    }


    private fun initMyProfile() {
        val profileImage =
            "https://images.unsplash.com/photo-1552058544-f2b08422138a?ixid=MXwxMjA3fDB8MHxzZWFyY2h8Nnx8cGVyc29ufGVufDB8fDB8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60"
        myUser = User(id = MY_USER_ID, profileImageUrl = profileImage, isOnline = true)
    }

    private fun initFriends() {
        val user1 = User(
            name = "Frank",
            profileImageUrl = sampleProfileImageUrlList[0],
            lastSeen = "10:12 AM"
        )


        val user3 = User(
            name = "Francis",
            profileImageUrl = sampleProfileImageUrlList[3],
            isOnline = true
        )

        val user4 = User(
            name = "Hanan",
            profileImageUrl = sampleProfileImageUrlList[4],
            isOnline = true
        )
        val user5 = User(
            name = "Vinny",
            profileImageUrl = sampleProfileImageUrlList[5],
            lastSeen = "11:13 AM"
        )
        val user6 = User(
            name = "Zahrah",
            profileImageUrl = sampleProfileImageUrlList[6],
            lastSeen = "09:42 AM"
        )
        val user7 = User(
            name = "Fergus",
            profileImageUrl = sampleProfileImageUrlList[7],
            isOnline = true
        )
        val user8 = User(
            name = "Iqrah",
            profileImageUrl = sampleProfileImageUrlList[8],
            lastSeen = "15:12 PM"
        )

        val user10 = User(
            name = "Irene",
            profileImageUrl = sampleProfileImageUrlList[9],
            isOnline = true
        )

        friendsList.add(user1)
        friendsList.add(user3)
        friendsList.add(user4)
        friendsList.add(user5)
        friendsList.add(user6)
        friendsList.add(user7)
        friendsList.add(user8)
        friendsList.add(user10)
    }

    //Generates random Normal chat for each friend and 2 group chat with random friends
    private fun initChatList() {

        //Generate normal chat(min 2 and max 20 messages) for each friend
        friendsList.forEach {
            val normalChat = NormalChat(it)
            val maxMessageNumber = Random.nextInt(2, 20)
            for (i in 0..maxMessageNumber) {
                val option = Random.nextInt(2) //Choose sender randomly me or other user
                val sender = if (option == 0) myUser else it
                val message =
                    Message(sender, Helper.getRandomText(), Helper.getRandomTimeString())
                normalChat.addMessage(message)

                //Adds Image message before last message
                if (i == maxMessageNumber - 1) {

                    val imageMessage =
                        Message(
                            sender = sender,
                            message = sampleProfileImageUrlList[Random.nextInt(
                                sampleProfileImageUrlList.size
                            )],
                            sendingTime = Helper.getRandomTimeString(),
                            type = Message.MessageType.IMAGE

                        )
                    normalChat.addMessage(imageMessage)
                }
            }

            chatList.add(normalChat)
        }


        //Generates 2 group chat with random friends
        for (i in 1..2) {

            val usersList = listOf(
                myUser,
                friendsList[i + 0],
                friendsList[i + 2],
                friendsList[i + 4]
            ).toMutableList()
            val groupChat = GroupChat(
                groupName = "Group Chat #$i", groupImage = sampleProfileImageUrlList[i],
                userList = usersList
            )

            //Generate random Messages
            val maxMessageNumber = Random.nextInt(2, 20)
            for (j in 0 until maxMessageNumber) {
                val sender = groupChat.userList[Random.nextInt(usersList.size)]
                val message =
                    Message(sender, Helper.getRandomText(), Helper.getRandomTimeString())
                groupChat.addMessage(message)
            }
            val groupChatIndex =
                if (i == 1) 2 else 6 //Adding Group chat to 2nd and 6th position
            chatList.add(groupChatIndex, groupChat)
            for(i in chatList.indices){
                chatList[i].id = i
            }
        }

    }
}