package com.example.mobprog.guild

import com.example.mobprog.user.User

class Guild (){
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var picture: String

    // change type to User
    private var guildMembers: ArrayList<User> = ArrayList()

    //change type to Events
    private var guildEvents: ArrayList<String> = ArrayList()

    constructor(guildName: String, guildDescription: String, guildPicture: String) : this(){
        name = guildName
        description = guildDescription
        picture = guildPicture
    }
    
    fun addGuildMember(user: User){
        guildMembers.add(user)
    }

    // add param event
    fun addEvent(){
        guildEvents.add("")
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

    fun getEvents(): ArrayList<String> {
        return guildEvents
    }

    fun getMembers(): ArrayList<User> {
        return guildMembers
    }

}