package com.example.mobprog.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

fun printAllUsers() {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val allUsers = db.collection("users")

    allUsers
        .get()
        .addOnSuccessListener { users ->
            for (user in users) {
                println("userid: ${user.id}, username: ${user.get("username")}, Password: ${user.get("password")}",)
            }
        }
}
