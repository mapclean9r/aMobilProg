package com.example.mobprog.data

import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.createEvent.EventManager
import com.example.mobprog.guild.Guild
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.collections.ArrayList

class GuildService {
    private val db = Firebase.firestore

    fun registerEventsToGuild(guild: Guild) {
        var events = db.collection("events")
        events.document("Friends of Legends").set(EventBase("Friends of Legends", 23, "small boy"))
        db.collection("guilds").document(guild.getGuildName()).set(guild)
    }

    fun getEventRef(guildEvents: EventManager): ArrayList<String>{
        var guildRef: ArrayList<String> = ArrayList()

        var events = db.collection("events").document().id

        for (e in guildEvents.getEvents()){
            for (i in events){
                if (e.getName() == i.toString()){
                    guildRef.add(i.toString())
                }
            }
        }
        return guildRef
    }

    fun registerEvent(event: EventBase){
        db.collection("events").add(event)
    }
}