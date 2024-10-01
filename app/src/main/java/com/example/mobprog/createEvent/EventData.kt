package com.example.mobprog.createEvent

import java.util.UUID

data class EventData(
    val name: String = "",
    val startDate: String = "",
    val creatorId: String = "",
    val description: String = "",
    val price: String = "Free",
    val location: String = "N/A",
    val picture: String = "",
    val host: String = "N/A",
    val date: String = "N/A",
    val comments: List<String> = emptyList(),
    val attendance: Int = 0,
    val members: List<String> = emptyList()
)
