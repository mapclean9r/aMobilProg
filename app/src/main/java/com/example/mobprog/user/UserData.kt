package com.example.mobprog.user

import java.util.UUID

data class UserData (
    val name: String = "",
    val password: String = "",
    val email: String = "",
    val nickname: String = "",
    val picture: String = "",
    val creatorId: String = generateId(),
    val friends: List<String> = emptyList()
)

fun generateId(): String {
    return UUID.randomUUID().toString()
}