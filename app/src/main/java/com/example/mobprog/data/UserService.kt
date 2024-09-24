package com.example.mobprog.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.mobprog.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

    fun printAllUsers() {
        val db = Firebase.firestore
        val allUsersRef = db.collection("users")

        allUsersRef
            .get()
            .addOnSuccessListener { users ->
                for (user in users) {
                    println(
                        "Userid: ${user.id}, Username: ${user.get("username")}, Password: ${
                            user.get(
                                "password"
                            )
                        }",
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Error getting documents: ", exception)
            }
    }

fun getUserWithId(id: String): Task<DocumentSnapshot> {
    val db = Firebase.firestore
    val userRef = db.collection("users").document(id).get()
    return userRef;
}

fun createUser(username: String, passoword: String) {
    val db = Firebase.firestore
    db.collection("users")
        .add(User(username, passoword))
        .addOnSuccessListener { Log.d(TAG, "User created!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error, cannot create user: ", e) }
}

fun checkIfUserExists(input: String) {
    val db = Firebase.firestore
    db.collection("users")
        .whereEqualTo("username", input)
        .get()
        .addOnSuccessListener { docs ->
            for (doc in docs) {
                Log.d(TAG, "${doc.id} => ${doc.data}")
            }
            return true
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error, could not find user: ", e)
        }
}