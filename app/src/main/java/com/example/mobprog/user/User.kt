package com.example.mobprog.user

import java.util.UUID


class User(private val email: String, private val username: String, private var password: String) {

    private var friends = ArrayList<User>()
    private var nickname = ""
    private var creatorId = generateId()

    constructor(email: String, username: String, password: String, friends: ArrayList<User>) : this(email, username, password) {
        this.friends = friends;
    }

    private fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    fun getCreatorId(): String {
        return creatorId
    }

    fun getEmail() : String {
        return email
    }

    fun getUsername() : String {
        return nickname.ifBlank { username }
    }

    fun getFriends() : ArrayList<User> {
        return friends
    }

    fun getPassword() : String {
        return password
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

    fun setPassword(pass: String) {
        password = pass;
        // TODO set new password to DB
    }

    fun setNickname(nick: String) {
        nickname = nick;
        // TODO set new nickname to DB
    }

}