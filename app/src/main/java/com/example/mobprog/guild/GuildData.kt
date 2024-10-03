package com.example.mobprog.guild

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

val calendar: Calendar = Calendar.getInstance()
@SuppressLint("ConstantLocale")
val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
val formattedDateTime: String = dateFormat.format(calendar.time)

data class GuildData(
    val name: String = "",
    val leader: String = "",
    val description: String = "",
    val picture: String = "",
    val creatorId: String = generateId(),
    val members: List<String> = emptyList(),
    val dateCreated: String = formattedDateTime
)

fun generateId(): String {
    return UUID.randomUUID().toString()
}