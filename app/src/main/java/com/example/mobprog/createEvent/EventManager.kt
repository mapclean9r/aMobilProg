package com.example.mobprog.createEvent

import com.example.mobprog.data.UserService
import com.example.mobprog.guild.Guild

class EventManager() {

    private var eventCollector: ArrayList<EventBase> = ArrayList()

    private var userDB = UserService();

    fun addEvent(event: EventBase){
        eventCollector.add(event)
    }

    fun removeEvent(event: EventBase){
        for (e in eventCollector){
            if(e == event){
                eventCollector.remove(e)
            }
        }
    }

    fun getEvents() : ArrayList<EventBase>{
        return eventCollector
    }

    fun sendDataGuild(guild: Guild){
        //sender data til Guild -> Eventmanager[Event]
        userDB.sendData(guild)
    }

    fun sendDataUser(){
        //sender data til User -> Eventmanager[Event]
    }

    fun getDataUser(){
        //henter data fra User -> Eventmanager[Event]
    }

    fun getDataGuild(){
        //hunter data fra Guild -> Eventmanager[Event]
    }
}
