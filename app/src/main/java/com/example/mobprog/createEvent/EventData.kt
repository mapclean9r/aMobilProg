package com.example.mobprog.createEvent

import java.util.UUID

data class EventData(
    val eventName: String = "",
    val eventDate: String = "",
    val creatorId: String = "",
    val description: String = "",
    val price: String = "Free",
    val location: String = "N/A",
    val picture: String = "",
    val host: String = "N/A",
    val date: String = "N/A",
    val comments: List<String> = emptyList(),
    val attenders: List<String> = emptyList(),
    val members: List<String> = emptyList()
)
