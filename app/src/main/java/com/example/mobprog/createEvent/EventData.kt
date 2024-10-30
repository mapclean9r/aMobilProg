package com.example.mobprog.createEvent

data class EventData(
    val name: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val creatorId: String = "",
    val description: String = "",
    val location: String = "",
    val comments: List<String> = emptyList(),
    val maxAttendance: Int = 0,
    val attending: List<String> = emptyList(),
    val image: String = "",
    val id: String = ""
)