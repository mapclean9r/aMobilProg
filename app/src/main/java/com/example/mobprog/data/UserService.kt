package com.example.mobprog.data

import android.util.Log
import com.example.mobprog.guild.Guild
import com.example.mobprog.user.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    fun getCurrentUserData(callback:  (Map<String, Any>?) -> Unit){
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").whereEqualTo("email", currentUserEmail)
        var userData: Map<String, Any>? = null

        docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val docs = task.result
                    if (docs != null && !docs.isEmpty) {
                        val doc = docs.first()

                        userData = doc.data
                        callback(doc.data)
                    }
                }
        }
    }

    //In Progress @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    fun sendData(guild: Guild) {
        db.collection("guild")
            .add(guild)
    }

}