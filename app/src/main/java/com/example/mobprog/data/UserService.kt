package com.example.mobprog.data

import android.util.Log
import androidx.compose.runtime.Composable
import com.example.mobprog.gui.friends.FriendData
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

    fun getUsernameWithDocID(documentId: String, callback: (String?) -> Unit) {
        val docRef = db.collection("users").document(documentId)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("name")
                    callback(username)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null)
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

    fun updatePassword(newPassword: String, callback: (Boolean, String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.updatePassword(newPassword)
                .addOnSuccessListener {
                    callback(true, "Password updated successfully!")
                }
                .addOnFailureListener { exception ->
                    val errorMessage = exception.message ?: "Failed to update password."
                    callback(false, errorMessage)
                }
        } else {
            callback(false, "User not authenticated.")
        }
    }

    fun getAllUsers(callback: (List<FriendData>?) -> Unit) {
        val users = arrayListOf<FriendData>()
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        db.collection("users").get()
            .addOnSuccessListener { doc ->
                if (!doc.isEmpty) {
                    for (document in doc) {
                        val username = document.getString("name")
                        val id = document.getString("id")
                        val accepted = document.getBoolean("accepted")
                        if (username != null && id != null && id != uid && accepted != null) {
                            val data = FriendData(id, username, accepted)
                            users.add(data)
                        }
                    }
                    // Pass the populated list to the callback
                    callback(users)
                } else {
                    // Pass an empty list if no documents were found
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error and pass null to the callback
                println("Error getting documents: $exception")
                callback(null)
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

    fun getUserFriends(callback: (ArrayList<FriendData>?) -> Unit) {
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
                val friendIds = mutableMapOf<String, Boolean>()
                for (document in documents) {
                    val friendId = document.getString("f_id")
                    val friendAccepted = document.getBoolean("accepted")
                    if (friendId != null && friendAccepted == true) {
                        friendIds[friendId] = friendAccepted
                    }
                }

                if (friendIds.isEmpty()) {
                    callback(ArrayList())
                    return@addOnSuccessListener
                }

                val friendNames = ArrayList<FriendData>()
                for (friendId in friendIds) {
                    usersRef.whereEqualTo("id", friendId.key).get()
                        .addOnSuccessListener { userDocument ->
                            if (!userDocument.isEmpty) {
                                val documentSnapshot = userDocument.documents[0]

                                val friendName = documentSnapshot.getString("name")
                                val id = documentSnapshot.getString("id")

                                if (friendName != null && id != null) {
                                    friendNames.add(FriendData(id, friendName, friendId.value))
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


        friendsRef.whereEqualTo("u_id", uid).whereEqualTo("f_id", friendId).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val friendData = hashMapOf(
                            "u_id" to uid,
                            "f_id" to friendId,
                            "accepted" to false
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