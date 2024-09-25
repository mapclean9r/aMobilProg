package com.example.mobprog.user


class User(private val email: String, private val username: String, private var password: String) {

    private val friends = ArrayList<User>()
    private var nickname = ""


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