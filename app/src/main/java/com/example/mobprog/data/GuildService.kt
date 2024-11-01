package com.example.mobprog.data

import com.example.mobprog.z_Old_Code.EventBase
import com.example.mobprog.z_Old_Code.EventManager
import com.example.mobprog.guild.GuildData
import com.example.mobprog.guild.formattedDateTime
import com.example.mobprog.guild.generateId
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.collections.ArrayList

class GuildService {
    val db = Firebase.firestore

    fun createGuild(guildData: GuildData, callback: (String?, Exception?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val guildsCollection = db.collection("guilds")
        val guildId = guildData.guildId

        guildsCollection.document(guildId).set(guildData)
            .addOnSuccessListener {
                callback(guildId, null)
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }

    fun parseGuildData(map: Map<String, Any>): GuildData? {
        return try {
            GuildData(
                name = map["name"] as? String ?: "",
                leader = map["leader"] as? String ?: "",
                description = map["description"] as? String ?: "",
                picture = map["picture"] as? String ?: "",
                guildId = map["guildId"] as? String ?: generateId(),
                members = map["members"] as? List<String> ?: emptyList(),
                dateCreated = map["dateCreated"] as? String ?: formattedDateTime
            )
        }
        catch (e: Exception) {
            e.printStackTrace()
            null
        }
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


    fun getAllGuilds(callback: (List<Map<String, Any>>?) -> Unit) {
        val allGuildsRef = db.collection("guilds")

        allGuildsRef
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result
                    if (doc != null && !doc.isEmpty) {
                        val guildDataMap = mutableListOf<Map<String, Any>>()
                        for (field in doc) {
                            field.data.let {
                                guildDataMap.add(it)
                            }
                        }
                        callback(guildDataMap)
                    }
                    else {
                        callback(null)
                    }
                }
            }
    }

    fun deleteGuildIfEmpty(guildId: String, callback: (Boolean, Exception?) -> Unit) {
        val guildRef = db.collection("guilds").document(guildId)

        guildRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val members = documentSnapshot.get("members") as? List<*>
                if (members.isNullOrEmpty()) {
                    guildRef.delete()
                        .addOnSuccessListener {
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            callback(false, exception)
                        }
                } else {
                    callback(false, null)
                }
            }
            .addOnFailureListener { exception ->
                callback(false, exception)
            }
    }

    fun transferGuildLeadership(guildId: String, currentLeaderId: String, newLeaderId: String, callback: (Boolean, Exception?) -> Unit) {
        getAllMemberIdsFromGuild(guildId) { members ->
            if (members == null) {
                callback(false, Exception("Failed to retrieve members for the guild."))
                return@getAllMemberIdsFromGuild
            }

            if (members.size <= 1) {
                callback(false, Exception("Transfer failed: No other members in the guild."))
                return@getAllMemberIdsFromGuild
            }

            if (!members.contains(newLeaderId)) {
                callback(false, Exception("Transfer failed: New leader is not a member of the guild."))
                return@getAllMemberIdsFromGuild
            }

            val guildRef = db.collection("guilds").document(guildId)
            guildRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val guildData = documentSnapshot.data?.let { parseGuildData(it) }
                    if (guildData != null && guildData.leader == currentLeaderId) {
                        guildRef.update("leader", newLeaderId)
                            .addOnSuccessListener {
                                callback(true, null)
                            }
                            .addOnFailureListener { exception ->
                                callback(false, exception)
                            }
                    } else {
                        callback(false, Exception("Transfer failed: Unauthorized action or guild not found."))
                    }
                }
                .addOnFailureListener { exception ->
                    callback(false, exception)
                }
        }
    }




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

    fun getAllMemberIdsFromGuild(guildId: String, callback: (List<String>?) -> Unit) {
        db.collection("guilds").document(guildId).get()
            .addOnSuccessListener { documentSnapshot ->
                try {
                    val members = documentSnapshot.get("members") as? List<String>
                    callback(members)
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }

}