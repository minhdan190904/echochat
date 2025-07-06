package com.example.echochat.network

interface Downloader  {
    fun downloadFile(url: String): Long
}