package com.example.mobprog.data

import android.util.Log
import com.example.mobprog.guild.Guild
import com.example.mobprog.user.User
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class UserService {
    private val db = Firebase.firestore
    private lateinit var userName: String
    val auth = Firebase.auth

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
        val newUser = User(email, username, password)
        db.collection("users")
            .add(newUser)
    }

    fun getUserWithId(id: String): Task<DocumentSnapshot> {
        val userRef = db.collection("users").document(id).get()
        return userRef;
    }

    fun getUserByEmail(inputEmail: String) {
        db.collection("users")
            .whereEqualTo("email", inputEmail)
            .get()
    }

    fun getEmailFromUserIfExists(inputEmail: String) {
        db.collection("users")
            .get()
            .addOnSuccessListener { users ->
                for (user in users) {

                    val emailField = user.getString("email")
                    if (emailField != null) {
                        println("Field Value: ")
                        println(emailField)
                        return@addOnSuccessListener
                    }
                }
                println("could not find email")
            }
    }


    fun loginAuthCheck(inputEmail: String, inputPassword: String) {
        var existingUser = getUserByEmail(inputEmail)
    }

    fun sendData(guild: Guild) {
        db.collection("guild")
            .add(guild)
    }

}