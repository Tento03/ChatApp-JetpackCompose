package com.example.chatappcompose.model

data class MessageRequest(
    val sender_id: String,
    val receiver_id: String,
    val message: String
)