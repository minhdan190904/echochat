package com.example.echochat.ai

import android.util.Log
import com.example.echochat.model.Chat
import com.example.echochat.model.Message
import com.example.echochat.model.ResponseWrapper
import com.example.echochat.util.myUser
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.google.gson.Gson

class GenerateResponse {
    suspend fun generateResponse(chat: Chat): List<String> {
        val jsonSchema = Schema.obj(
            mapOf(
                "listResponse" to Schema.array(
                    Schema.obj(
                        mapOf(
                            "responses" to Schema.array(
                                Schema.obj(
                                    mapOf(
                                        "response" to Schema.string(),
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )

        val listMessageWithName = chat.messageList.map { message ->
            "${message.sender?.name}: ${message.message}"
        }.joinToString("\n")

        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.5-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = jsonSchema
            })

        val prompt =
            "Đây là đoạn hội thoại giữa hai người. Trả về JSON gồm mảng các câu trả lời GỢI Ý của người ${myUser?.name} sau câu cuộc hội thoại trên dù ${myUser?.name} đang nói hay đang nghe. Không cần giải thích, chỉ trả JSON.\n" +
                    "Tất nhiên cuộc hội thoại phải dựa vào và trả lời theo ý nghĩ con người không phải máy móc.\n" +
                    "Tầm 5 đến 7 cái gợi ý thôi nhé. \n" +
                    "\n" + listMessageWithName
        val response = model.generateContent(prompt)
        val json = response.text!!
        val gson = Gson()

        val parsed = gson.fromJson(json, ResponseWrapper::class.java)
        val responseList: List<String> = parsed.listResponse
            .flatMap { it.responses }
            .map { it.response }

        responseList.forEach {
            Log.i("ParsedResponse", it)
        }
        return responseList
    }
}