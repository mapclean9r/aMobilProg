package com.example.mobprog.user



class User(private var id: Int = -1, val name: String, var password: String) {

    init {
        if(id < 0) { // Check if user exists in db.
            //TODO get user from db
        }
    }

}