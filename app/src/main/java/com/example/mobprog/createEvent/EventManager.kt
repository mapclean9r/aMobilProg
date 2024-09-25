package com.example.mobprog.createEvent

class EventManager() {

    private var eventCollector: ArrayList<EventBase> = ArrayList()

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
}
