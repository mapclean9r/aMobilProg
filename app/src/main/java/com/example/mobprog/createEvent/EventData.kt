package com.example.mobprog.createEvent

data class EventData(
    val name: String = "",
    val startDate: String = "N/A",
    val endDate: String = "N/A",
    val creatorId: String = "",
    val description: String = "",
    val price: String = "Free",
    val location: String = "N/A",
    val host: String = "N/A",
    val comments: List<String> = emptyList(),
    val maxAttendance: Int = 0,
    val attending: List<String> = emptyList(),
    val image: String = "",
    val id: String = ""
)