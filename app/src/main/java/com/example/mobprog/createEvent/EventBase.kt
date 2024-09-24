package com.example.mobprog.createEvent

import com.example.mobprog.user.User

class EventBase(private var eventName: String, private var eventMaxAttendance: Int, private var eventPicture: String) {
    private var eventComments: ArrayList<EventComment> = ArrayList()
    private var currentAttenders: ArrayList<User> = ArrayList()
    private var eventGames: ArrayList<String> = ArrayList()

    //Update datatype when added
    private lateinit var eventLocation: String
    private var price: String = "-1"

    fun addComment(user: User, comment: String){
        eventComments.add(EventComment(user, comment))
    }

    fun addGame(game: String){
        eventGames.add(game)
    }

    fun deleteGame(game: String){
        for (i in eventGames){
            if (game == i){
                eventGames.remove(game)
                return
            }
        }
    }

    fun updateLocation(newLocation: String){
        eventLocation = newLocation
    }

    fun addParticipants(user: User){
        if(currentAttenders.size == eventMaxAttendance){
            return
        }
        currentAttenders.add(user)
    }

    fun getName(): String{
        return eventName
    }

    fun getAttendance(): Int{
        return eventMaxAttendance
    }

    fun getPicture(): String{
        return eventPicture
    }

    fun getComments(): ArrayList<EventComment> {
        return eventComments
    }

    fun getParticipants(): ArrayList<User> {
        return currentAttenders
    }

    fun getLocation(): String {
        return eventLocation
    }

    fun getPrice(): String {
        if (price == "-1"){
            return "Free"
        }
        return price
    }

    fun setLocation(newLocation: String){
        eventLocation = newLocation
    }

    fun setName(newName: String){
        eventName = newName
    }

    fun setAttendance(newAttendance: Int){
        eventMaxAttendance = newAttendance
    }

    fun setPicture(newPicture: String){
        eventPicture = newPicture
    }

    fun setPrice(newPrice: String){
        price = newPrice
    }
}