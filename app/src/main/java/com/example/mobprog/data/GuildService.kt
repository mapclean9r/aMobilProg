package com.example.mobprog.data

import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.guild.Guild
import com.example.mobprog.user.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

class GuildService {
    private val db = Firebase.firestore

    fun registerEventsToGuild(guild: Guild) {
        db.collection("guilds").document(guild.getGuildName()).set(guild)
    }

    fun registerEvent(event: EventBase){
        db.collection("events").add(event)
    }
}