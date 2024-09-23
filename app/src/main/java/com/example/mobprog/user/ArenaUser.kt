package com.example.mobprog.user


class ArenaUser(private var id: String = "", val name: String, var password: String) {

    constructor(name: String, password: String
    ): this("", name, password) {

    }

    init {
        if (id.isBlank()) {
            // TODO get user from name and password, throw exception if not found(?)
        }
    }

}