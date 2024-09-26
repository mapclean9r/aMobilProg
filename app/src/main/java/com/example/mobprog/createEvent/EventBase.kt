package com.example.mobprog.createEvent

import com.example.mobprog.user.User

class EventBase(private var eventName: String, private var eventMaxAttendance: Int, private var eventPicture: String) {
    private val eventComments = ArrayList<EventComment>()
    private val currentAttenders = ArrayList<User>()
    private val eventGames = ArrayList<String>()

    //Update datatype when added
    private var price: String = "-1"
    private var location: String = "N/A"
    private var eventStartDate: String = "N/A"
    private var host: String = "N/A"

    fun addComment(user: User, comment: String){
        eventComments.add(EventComment(user, comment))
    }

    fun addGame(game: String){
        eventGames.add(game)
    }

    fun addLocation(newLocation: String){
        location = newLocation
    }

    fun addEventStartDate(newDate: String){
        eventStartDate = newDate;
    }

    fun deleteGame(game: String){
        for (i in eventGames){
            if (game == i){
                eventGames.remove(game)
                return
            }
        }
    }

    fun setHost(newHost: String){
        host = newHost
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

    fun getLocation(): String{
        return location
    }

    fun getStartDate(): String{
        return eventStartDate
    }

    fun getHost(): String {
        return host
    }

    fun getPrice(): String {
        if (price == "-1"){
            return "Free"
        }
        return price
    }

}