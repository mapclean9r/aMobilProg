package com.example.mobprog.z_Old_Code

import com.example.mobprog.data.GuildService

class EventManager() {

    private var eventCollector: ArrayList<EventBase> = ArrayList()

    private var guildDB = GuildService();

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
