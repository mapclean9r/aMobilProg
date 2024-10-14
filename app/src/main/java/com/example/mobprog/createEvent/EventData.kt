package com.example.mobprog.createEvent

data class EventData(
    val name: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val creatorId: String = "",
    val description: String = "",
    val price: String = "Free",
    val location: String = "",
    val comments: List<String> = emptyList(),
    val maxAttendance: Number = 0,
    val attending: List<String> = emptyList(),
    val image: String = "",
    val id: String = ""
)