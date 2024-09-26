package com.example.mobprog.data

import android.content.ContentValues.TAG
import android.util.Log
import com.example.mobprog.guild.Guild
import com.example.mobprog.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class UserService {
    private val db = Firebase.firestore
    private lateinit var userName: String

    fun getUsername(user: String) : String {
        return "e"
    }

    fun printAllUsers() {
        val allUsersRef = db.collection("users")

        allUsersRef
            .get()
            .addOnSuccessListener { users ->
                for (user in users) {
                    println(
                        "Userid: ${user.id}, Username: ${user.get("name")}, Password: ${
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

    fun createUser(email: String, username: String, password: String) {
        val user = User(email, username, password)
        val userRef = db.collection("users")
        val query = userRef.whereEqualTo("email", email).get()

        if (query.result != null) {
            db.collection("users")
                .add(user)
                .addOnSuccessListener { Log.d(TAG, "User created!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error, cannot create user: ", e) }
        }
    }

    fun getUserWithId(id: String): Task<DocumentSnapshot> {
        val userRef = db.collection("users").document(id).get()
        return userRef;
    }

    fun getUserByEmail(inputEmail: String) {
        db.collection("users")
            .whereEqualTo("email", inputEmail)
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

    fun loginAuthCheck(inputUsername: String, inputPassword: String) {
        db.collection("users")
            .get()
            .addOnSuccessListener { doc ->
                for (field in doc) {
                    val usernameField = field.getString("name")
                    val passwordField = field.getString("password")
                    println(usernameField)
                    println(passwordField)

                    if (usernameField == inputUsername && passwordField == inputPassword) {

                        println("Found a match in document ID: ${field.id}")
                    }
                }
            }
            .addOnFailureListener { e ->
                println("Error getting documents: $e")
            }

    }

    fun sendData(guild: Guild) {
        db.collection("guild")
            .add(guild)
    }

}