package com.example.mobprog.z_Old_Code

import com.example.mobprog.data.GuildService

class Guild (){
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var picture: String
    private var guildMembers: ArrayList<User> = ArrayList()
    private lateinit var guildEvents: EventManager

    constructor(guildName: String, guildDescription: String, guildPicture: String) : this(){
        name = guildName
        description = guildDescription
        picture = guildPicture
        //add user to guild and make owner (owner verification level) when created
    }

    fun addGuildMember(user: User){
        guildMembers.add(user)
    }

    fun addEventManager(eventManager: EventManager){
        guildEvents = eventManager
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
        return guildEvents.getEvents()
    }

    fun getEventsRef(): ArrayList<String> {
        return GuildService().getEventRef(guildEvents)
    }

    fun getMembers(): ArrayList<User> {
        return guildMembers
    }


}