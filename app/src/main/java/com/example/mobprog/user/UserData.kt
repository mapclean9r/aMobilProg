package com.example.mobprog.user

import com.example.mobprog.data.UserService
import java.util.UUID

data class UserData (
    val name: String = "",
    val password: String = "",
    val email: String = "",
    val picture: String = "",
    val creatorId: String = generateId(),
    val friends: List<String> = getFriends(),
    val guild: String = ""
)

fun generateId(): String {
    return UUID.randomUUID().toString()
}

fun getFriends(): List<String> {
    UserService().getUserFriends { success ->
        if(success != null) {
            return@getUserFriends
        }
    }
    return emptyList()
}
