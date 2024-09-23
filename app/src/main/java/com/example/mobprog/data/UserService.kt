package com.example.mobprog.data

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

    fun printAllUsers() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
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


    //TODO Metoden under er ment for å hente en bruker basert på id, (beste hadde vært navn, men kan lage ny metode for det). Metoden fungerer ikke for øyeblikket
    suspend fun getUserWithId(id: String): DocumentSnapshot? {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        return try {
            val usersCollection = db.collection("users");
            usersCollection.document(id).get().await()
        } catch (e: Exception) {
            println("The user dosent exist.")
            return null;
        }
    }
