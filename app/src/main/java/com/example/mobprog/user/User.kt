package com.example.mobprog.user



class User(private var id: Int = -1, val name: String, var password: String) {

    constructor(name: String, password: String
    ): this(-1, name, password) {

    }

    init {
        if(id < 0) { // Check if user exists in db.
            //TODO get user from db
        }
    }

}