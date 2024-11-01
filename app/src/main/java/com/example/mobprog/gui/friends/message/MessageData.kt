package com.example.mobprog.gui.friends.message

import com.google.firebase.Timestamp


data class MessageData(
    val senderId: String = "",
    val content: String = "",
    val timestamp: Timestamp? = null
)