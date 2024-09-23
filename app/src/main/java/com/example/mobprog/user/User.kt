package com.example.mobprog.user

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class User(var id: String = "", val name: String, var password: String, private var isOnline: Boolean = false) {

    private val friends = ArrayList<User>()


    constructor(name: String, password: String
    ): this("", name, password) {

    }

    init {
        if (id.isBlank()) {
            // TODO get user from name and password, throw exception if not found(?)


            // TODO get friends from DB and add into friends list
            isOnline = true;
        }
    }

    fun isOnline() : Boolean {
        return isOnline;
    }

    fun getFriends() : ArrayList<User> {
        return friends
    }

    fun addFriend(user: User) : User {
        friends.add(user)
        // TODO add friend to DB
        return this
    }

    fun removeFriend(user: User) : User {
        friends.remove(user)
        // TODO Remove friend from DB
        return this;
    }

}