package com.example.mobprog.user

import android.annotation.SuppressLint
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.UserService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

val calendar: Calendar = Calendar.getInstance()
@SuppressLint("ConstantLocale")
val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
val formattedDateTime: String = dateFormat.format(calendar.time)

data class UserData(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val picture: String = "",
    val friends: List<String> = getFriends(),
    val guild: String = "",
    val dateCreated: String = formattedDateTime,
    val eventsToAttend: List<String> = emptyList()
)

fun getFriends(): List<String> {
    FriendService().getAllFriends { success ->
        if(success != null) {
            return@getAllFriends
        }
    }
    return emptyList()
}

