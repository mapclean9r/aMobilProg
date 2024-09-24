package com.example.mobprog.createEvent

import com.example.mobprog.user.User

class EventComment(private var user: User, private var comment: String) {

    fun getUser(): User {
        return user;
    }

    fun getComment(): String {
        return comment;
    }

    fun setComment(newComment: String){
        comment = newComment;
    }
}