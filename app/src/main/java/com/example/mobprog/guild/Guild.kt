package com.example.mobprog.guild

class Guild (){
    private lateinit var name: String;
    private lateinit var description: String;
    private lateinit var picture: String;

    // change type to User
    private lateinit var guildMembers: List<String>;

    //change type to Events
    private lateinit var guildEvent: List<String>;

    constructor(guildName: String, guildDescription: String, guildPicture: String) : this(){
        name = guildName;
        description = guildDescription;
        picture = guildPicture;
    }

    // add param User
    fun addGuildMember(){

    }

    // add param User
    fun joinGuild(){

    }

    // add param event
    fun addEvent(){

    }




}