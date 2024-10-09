package com.example.mobprog.data

import android.util.Log
import com.example.mobprog.user.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class UserService {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    fun createUser(email: String, username: String, password: String, callback: (Boolean, Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid

                    if (uid != null) {
                        val newUser = UserData(
                            id = uid,
                            email = email,
                            name = username
                        )

                        db.collection("users").document(uid).set(newUser)
                            .addOnSuccessListener {
                                callback(true, null)
                            }
                            .addOnFailureListener { exception ->
                                callback(false, exception)
                            }
                    } else {
                        callback(false, Exception("Failed to retrieve user UID"))
                    }
                } else {
                    val exception = task.exception
                    callback(false, exception)
                }
            }
    }

    fun getCurrentUserData(callback:  (Map<String, Any>?) -> Unit){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userRef = db.collection("users").document(uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val data = document.data
                        callback(data)
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                    callback(null)
                }
        }
    }

    fun updateUserProfile(name: String, picture: String, callback: (Boolean, Exception?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            val userRef = db.collection("users").document(uid)
            userRef.update(mapOf(
                "name" to name,
                "picture" to picture
            )).addOnSuccessListener {
                callback(true, null)
            }.addOnFailureListener { exception ->
                callback(false, exception)
            }
        } else {
            callback(false, Exception("User not authenticated"))
        }
    }


    fun getCurrentUserGuild(callback: (String?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(null)
            return
        }

        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)

        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val guild = document.getString("guild")
                    callback(guild)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }


    fun updateUserGuild(guildId: String?, callback: (Boolean, Exception?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(false, Exception("User not authenticated"))
            return
        }

        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(uid)

        db.runTransaction { transaction ->

            val userSnapshot = transaction.get(userRef)
            if (!userSnapshot.exists()) {
                throw Exception("User document does not exist")
            }
            val previousGuildId = userSnapshot.getString("guild")

            var guildRef: DocumentReference? = null
            var guildSnapshot: DocumentSnapshot? = null
            if (!guildId.isNullOrEmpty()) {
                guildRef = db.collection("guilds").document(guildId)
                guildSnapshot = transaction.get(guildRef)
                if (!guildSnapshot.exists()) {
                    throw Exception("Guild does not exist")
                }
            }

            var previousGuildRef: DocumentReference? = null
            var previousGuildSnapshot: DocumentSnapshot? = null
            if (!previousGuildId.isNullOrEmpty() && previousGuildId != guildId) {
                previousGuildRef = db.collection("guilds").document(previousGuildId)
                previousGuildSnapshot = transaction.get(previousGuildRef)
                if (!previousGuildSnapshot.exists()) {
                    throw Exception("Previous guild does not exist")
                }
            }

            transaction.update(userRef, "guild", guildId ?: "")

            if (guildRef != null) {
                transaction.update(guildRef, "members", FieldValue.arrayUnion(uid))
            }

            if (previousGuildRef != null) {
                transaction.update(previousGuildRef, "members", FieldValue.arrayRemove(uid))
            }
        }.addOnSuccessListener {
            callback(true, null)
        }.addOnFailureListener { exception ->
            exception.printStackTrace()
            callback(false, exception)
        }
    }

    // FRIENDS

    fun getUserFriends(callback: (ArrayList<String>?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(null)
            return
        }


        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()


        val friendsRef = db.collection("friends")
        val usersRef = db.collection("users")


        friendsRef.whereEqualTo("u_id", uid).get()
            .addOnSuccessListener { documents ->
                val friendIds = ArrayList<String>()
                for (document in documents) {
                    val friendId = document.getString("f_id")
                    if (friendId != null) {
                        friendIds.add(friendId)
                    }
                }

                if (friendIds.isEmpty()) {
                    callback(null)
                    return@addOnSuccessListener
                }

                val friendNames = ArrayList<String>()
                for (friendId in friendIds) {
                    usersRef.whereEqualTo("creatorId", friendId).get()
                        .addOnSuccessListener { userDocument ->
                            if (!userDocument.isEmpty) {
                                val documentSnapshot = userDocument.documents[0]

                                val friendName = documentSnapshot.getString("name")

                                if (friendName != null) {
                                    friendNames.add(friendName)
                                }

                                callback(friendNames)

                            }
                        }
                        .addOnFailureListener {
                            callback(null)
                        }
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun addFriend(friendId: String, callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(false)
            return
        }
        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        val friendsRef = db.collection("friends")
        val usersRef = db.collection("users")
        var userExists = false
        usersRef.whereEqualTo("creatorId", friendId).get()
            .addOnSuccessListener { userDocument ->
                if(userDocument.isEmpty) {
                    userExists = true
                }
            }

        if(userExists) {
            friendsRef.whereEqualTo("u_id", uid).whereEqualTo("f_id", friendId).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val friendData = hashMapOf(
                            "u_id" to uid,
                            "f_id" to friendId
                        )
                        friendsRef.add(friendData)
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener {
                                callback(false)
                            }
                    } else {
                        callback(false)
                    }
                }
                .addOnFailureListener {
                    callback(false)
                }
        } else {
            callback(false)
        }
    }

    fun removeFriend(friendId: String, callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(false)
            return
        }
        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        val friendsRef = db.collection("friends")

        friendsRef.whereEqualTo("u_id", uid).whereEqualTo("f_id", friendId).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        friendsRef.document(document.id).delete()
                            .addOnSuccessListener {
                                callback(true)
                            }
                            .addOnFailureListener {
                                callback(false)
                            }
                    }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }


}