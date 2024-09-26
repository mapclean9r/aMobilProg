package com.example.mobprog.guild

import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.user.User
import java.io.Serializable

class Guild (){
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var picture: String
    private var guildMembers: ArrayList<User> = ArrayList()
    private var guildEvents: ArrayList<EventBase> = ArrayList()


    constructor(guildName: String, guildDescription: String, guildPicture: String) : this(){
        name = guildName
        description = guildDescription
        picture = guildPicture
        //add user to guild and make owner (owner verification level) when created
    }

    fun addGuildMember(user: User){
        guildMembers.add(user)
    }

    fun addEvent(event: EventBase){
        guildEvents.add(event)
    }

    fun deleteEvent(event: EventBase){
        for (events in guildEvents){
            if (event == events){
                guildEvents.remove(event)
                return
            }
        }
    }

    fun setPicture(newPicture: String){
        picture =  newPicture
    }

    fun setDescription(newDescription: String){
        description = newDescription
    }

    fun setGuildName(newGuildName: String){
        description = newGuildName
    }

    fun getGuildName(): String {
        return name
    }

    fun getGuildDescription(): String {
        return description
    }

    fun getGuildPicture(): String {
        return picture
    }

    fun getEvents(): ArrayList<EventBase> {
        return guildEvents
    }

    fun getMembers(): ArrayList<User> {
        return guildMembers
    }


}