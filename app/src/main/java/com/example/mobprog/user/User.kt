package com.example.mobprog.user


class User(

    private val username: String,
    private var password: String,
    private var isOnline: Boolean = false) {

    private val friends = ArrayList<User>()
    private var nickname = ""


    constructor(username: String, password: String
    ): this(username, password, isOnline = false)

    fun getName() : String {
        return nickname.ifBlank { username }
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

    fun setPassword(pass: String) {
        password = pass;
        // TODO set new password to DB
    }

    fun setNickname(nick: String) {
        nickname = nick;
        // TODO set new nickname to DB
    }

}