package com.example.mobprog.guild

import java.util.UUID

data class GuildData(
    val guildName: String = "",
    val guildLeader: String = "",
    val guildDescription: String = "",
    val guildPicture: String = "",
    val creatorId: String = generateId(),
    val members: List<String> = emptyList()
)

fun generateId(): String {
    return UUID.randomUUID().toString()
}