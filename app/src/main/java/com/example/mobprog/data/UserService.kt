package com.example.mobprog.data

import android.util.Log
import com.example.mobprog.guild.Guild
import com.example.mobprog.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class UserService {
    private val db = Firebase.firestore

    //Fungerer 100% @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

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

    fun getEmailFromUserIfExists(inputEmail: String, callback: (String?) -> Unit) {
        var emailField: String? = null

        db.collection("users")
            .get()
            .addOnSuccessListener { users ->
                for (user in users) {
                    val email = user.getString("email")
                    if (email != null && email == inputEmail) {
                        emailField = email
                        break
                    }
                }
                callback(emailField)
            }
    }

    //In Progress @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    fun getCurrentUserByEmail(){
        db.collection("users")
            .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email.toString())
            .get()
    }

    fun sendData(guild: Guild) {
        db.collection("guild")
            .add(guild)
    }

}