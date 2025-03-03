package com.example.echochat.network.socket

import okhttp3.WebSocketListener

import okhttp3.*
import okio.ByteString

class MyWebSocketClient : WebSocketListener() {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder()
            .url("wss://echo.websocket.org")
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client đóng kết nối")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("WebSocket đã kết nối!")
        webSocket.send("Hello Server!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("Nhận tin nhắn: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Nhận tin nhắn dạng byte: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("Đang đóng kết nối: $code - $reason")
        webSocket.close(1000, null)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket đã đóng: $code - $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("Lỗi WebSocket: ${t.message}")
    }
}
