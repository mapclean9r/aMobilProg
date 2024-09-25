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

fun createUser(username: String, password: String) {
    val user = User(username, password)
    val db = Firebase.firestore
    db.collection("users")
        .add(user)
        .addOnSuccessListener { Log.d(TAG, "User created!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error, cannot create user: ", e) }
}

fun getUserByUsername(input: String) {
    val db = Firebase.firestore
    db.collection("users")
        .whereEqualTo("username", input)
        .get()
        .addOnSuccessListener { docs ->
            for (doc in docs) {
                Log.d(TAG, "${doc.id} => ${doc.data}")
            }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Error, could not find user: ", e)
        }
}

fun checkIfUsernameAndPasswordIsCorrect(inputUsername: String, inputPassword: String): Boolean {
    val db = Firebase.firestore
    var correctUser = false
    db.collection("users")
        .get()
        .addOnSuccessListener { docs ->
            for (document in docs) {
                val usernameField = document.getString("username")
                val passwordField = document.getString("password")

                if (usernameField == inputUsername && passwordField == inputPassword) {
                    println("Found a match in document ID: ${document.id}")
                    correctUser = true
                    break
                }
            }
            if (!correctUser) {
                println("No document found with matching field value.")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting documents: $exception")
        }
    return correctUser;
}
