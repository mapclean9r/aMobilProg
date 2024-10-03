package com.example.mobprog.data

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

val calendar: Calendar = Calendar.getInstance()
@SuppressLint("ConstantLocale")
val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
val formattedDateTime: String = dateFormat.format(calendar.time)

data class UserData (
    val name: String = "",
    val password: String = "",
    val email: String = "",
    val displayName: String = "",
    val dateCreated: String = formattedDateTime,
    val picture: String = "",
    val creatorId: String = generateId(),
    val friends: List<String> = emptyList(),
    val guild: String = "",
    )

   fun generateId(): String {
        return UUID.randomUUID().toString()
   }