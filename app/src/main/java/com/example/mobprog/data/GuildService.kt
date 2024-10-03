package com.example.mobprog.data

import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.createEvent.EventManager
import com.example.mobprog.guild.Guild
import com.example.mobprog.guild.GuildData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.collections.ArrayList

class GuildService {
    val db = Firebase.firestore

    //Fungerer 100% @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    fun createGuild(guild: GuildData) {
        db.collection("guilds").add(guild)
    }

    private fun parseGuildData(map: Map<String, Any>): GuildData {
        return GuildData(
            name = map["name"] as? String ?: "",
            leader = map["leader"] as? String ?: "",
            description = map["description"] as? String ?: "",
            picture = map["picture"] as? String ?: "",
            creatorId = map["creatorId"] as? String ?: generateId(),
            members = map["members"] as? List<String> ?: emptyList(),
            dateCreated = map["dateCreated"] as? String ?: formattedDateTime
        )
    }


    fun getCurrentUserGuildData(callback: (GuildData?) -> Unit) {
        UserService().getCurrentUserData { userData ->
            if (userData != null) {
                val guildId = userData["guild"] as? String
                if (!guildId.isNullOrEmpty()) {
                    val db = FirebaseFirestore.getInstance()
                    val guildRef = db.collection("guilds").document(guildId)
                    guildRef.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val doc = task.result
                            if (doc != null && doc.exists()) {
                                val guildDataMap = doc.data
                                if (guildDataMap != null) {
                                    val guildData = parseGuildData(guildDataMap)
                                    callback(guildData)
                                } else {
                                    callback(null)
                                }
                            } else {
                                callback(null)
                            }
                        } else {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            } else{
                callback(null)
            }
        }
    }


    fun registerEvent(event: EventBase){
        db.collection("events").add(event)
    }

    //In Progress @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    fun getEventRef(guildEvents: EventManager): ArrayList<String>{
        val guildRef: ArrayList<String> = ArrayList()

        val events = db.collection("events").document().id

        for (e in guildEvents.getEvents()){
            for (i in events){
                if (e.getName() == i.toString()){
                    guildRef.add(i.toString())
                }
            }
        }
        return guildRef
    }


    //
    fun createEventsForGuild(guildData: GuildData) {
        db.collection("events").add(guildData)
    }

    fun fetchEventsForGuild(guildId: String) {
        db.collection("events").whereEqualTo("creatorId", guildId).get()
    }

}