package com.example.mobprog.data

import android.util.Log
import com.example.mobprog.user.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

fun printAllUsers() {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val allUsersRef = db.collection("users")

    allUsersRef
        .get()
        .addOnSuccessListener { users ->
            for (user in users) {
                println("Userid: ${user.id}, Username: ${user.get("username")}, Password: ${user.get("password")}",)
            }
        }
        .addOnFailureListener { exception ->
            Log.w("Error getting documents: ", exception)
        }
}

fun getUserWithId(id: String): Unit {
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(id)

    userRef.get().addOnSuccessListener { documentSnapshot ->
        val user = documentSnapshot.toObject<User>();
    }
}