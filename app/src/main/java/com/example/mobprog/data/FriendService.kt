package com.example.mobprog.data

import com.example.mobprog.gui.friends.FriendData
import com.example.mobprog.gui.friends.message.MessageData
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FriendService {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()


    fun getAllFriends(callback: (ArrayList<FriendData>?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(null)
            return
        }

        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val friendsRef = db.collection("friends")
                val usersRef = db.collection("users")

                val friendIds = mutableMapOf<String, Boolean>() // ID -> accepted status

                // Fetch friends where current user is `u_id` or `f_id`
                val query1 = friendsRef.whereEqualTo("u_id", uid).get().await()
                val query2 = friendsRef.whereEqualTo("f_id", uid).get().await()

                // Process results from both queries

                query1.documents.forEach { document ->
                    val friendId = document.getString("f_id")
                    val friendAccepted = document.getBoolean("accepted")
                    if (friendId != null && friendAccepted != null) {
                        friendIds[friendId] = friendAccepted
                    }
                }


                query2.documents.forEach { document ->
                    val friendId = document.getString("u_id")
                    val friendAccepted = document.getBoolean("accepted")
                    if (friendId != null && friendAccepted != null) {
                        friendIds[friendId] = friendAccepted
                    }
                }

                if (friendIds.isEmpty()) {
                    callback(ArrayList()) // No friends
                    return@launch
                }

                // Fetch all friends' data at once using `whereIn`
                val userIds = friendIds.keys.toList()
                val userDocuments = usersRef.whereIn("id", userIds).get().await()

                val friendDataList = ArrayList<FriendData>()
                for (document in userDocuments.documents) {
                    val friendName = document.getString("name")
                    val id = document.getString("id")
                    val accepted = friendIds[id] // Get accepted status from map

                    if (friendName != null && id != null && accepted != null) {
                        friendDataList.add(FriendData(id, friendName, accepted))
                    }
                }

                callback(friendDataList) // Return the result

            } catch (e: Exception) {
                callback(null) // Handle any errors
            }
        }
    }

    fun getUserFriends(callback: (ArrayList<FriendData>?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(null)
            return
        }

        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val friendsRef = db.collection("friends")
                val usersRef = db.collection("users")

                val friendIds = mutableMapOf<String, Boolean>() // ID -> accepted status

                // Fetch friends where current user is `u_id` or `f_id`
                val query2 = friendsRef.whereEqualTo("f_id", uid).get().await()

                // Process results from both queries



                query2.documents.forEach { document ->
                    val friendId = document.getString("u_id")
                    val friendAccepted = document.getBoolean("accepted")
                    if (friendId != null && friendAccepted != null) {
                        friendIds[friendId] = friendAccepted
                    }
                }

                if (friendIds.isEmpty()) {
                    callback(ArrayList()) // No friends
                    return@launch
                }

                // Fetch all friends' data at once using `whereIn`
                val userIds = friendIds.keys.toList()
                val userDocuments = usersRef.whereIn("id", userIds).get().await()

                val friendDataList = ArrayList<FriendData>()
                for (document in userDocuments.documents) {
                    val friendName = document.getString("name")
                    val id = document.getString("id")
                    val accepted = friendIds[id] // Get accepted status from map

                    if (friendName != null && id != null && accepted != null) {
                        friendDataList.add(FriendData(id, friendName, accepted))
                    }
                }

                callback(friendDataList) // Return the result

            } catch (e: Exception) {
                callback(null) // Handle any errors
            }
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

    fun acceptFriendRequest(friendId: String, callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(false)
            return
        }

        val uid = currentUser.uid
        val db = FirebaseFirestore.getInstance()
        val friendsRef = db.collection("friends")

        // Query for the friend request where the current user is either `u_id` or `f_id`
        friendsRef
            .whereEqualTo("accepted", false)
            .whereIn("u_id", listOf(uid, friendId)) // Check if the current user is `u_id` or `f_id`
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Check the reverse case: current user as `f_id` and the friend as `u_id`
                    friendsRef
                        .whereEqualTo("accepted", false)
                        .whereIn("f_id", listOf(uid, friendId))
                        .get()
                        .addOnSuccessListener { reverseDocuments ->
                            if (reverseDocuments.isEmpty) {
                                callback(false) // No pending friend request found
                            } else {
                                // Update the "accepted" field to true for the reverse case
                                val documentId = reverseDocuments.documents[0].id
                                friendsRef.document(documentId)
                                    .update("accepted", true)
                                    .addOnSuccessListener {
                                        callback(true) // Friend request accepted
                                    }
                                    .addOnFailureListener {
                                        callback(false) // Error during update
                                    }
                            }
                        }
                        .addOnFailureListener {
                            callback(false) // Error during reverse query
                        }
                } else {
                    // Update the "accepted" field to true for the first case
                    val documentId = documents.documents[0].id
                    friendsRef.document(documentId)
                        .update("accepted", true)
                        .addOnSuccessListener {
                            callback(true) // Friend request accepted
                        }
                        .addOnFailureListener {
                            callback(false) // Error during update
                        }
                }
            }
            .addOnFailureListener {
                callback(false) // Error fetching the friend request
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

        // First, check where `u_id` is the current user and `f_id` is the friend
        friendsRef.whereEqualTo("u_id", uid).whereEqualTo("f_id", friendId).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Delete all documents where `u_id` is `uid` and `f_id` is `friendId`
                    for (document in documents) {
                        friendsRef.document(document.id).delete()
                            .addOnSuccessListener {
                                callback(true) // Friend successfully removed
                            }
                            .addOnFailureListener {
                                callback(false) // Failed to delete document
                            }
                    }
                } else {
                    // Check the reverse case where `u_id` is `friendId` and `f_id` is `uid`
                    friendsRef.whereEqualTo("u_id", friendId).whereEqualTo("f_id", uid).get()
                        .addOnSuccessListener { reverseDocuments ->
                            if (!reverseDocuments.isEmpty) {
                                // Delete all documents where `u_id` is `friendId` and `f_id` is `uid`
                                for (document in reverseDocuments) {
                                    friendsRef.document(document.id).delete()
                                        .addOnSuccessListener {
                                            callback(true) // Friend successfully removed
                                        }
                                        .addOnFailureListener {
                                            callback(false) // Failed to delete document
                                        }
                                }
                            } else {
                                callback(false) // No friend relationship found
                            }
                        }
                        .addOnFailureListener {
                            callback(false) // Failed during reverse query
                        }
                }
            }
            .addOnFailureListener {
                callback(false) // Error during first query
            }
    }

    fun getFriendRelationshipId(
        friendId: String,
        callback: (String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val friendsRef = db.collection("friends")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(null)
            return
        }

        val uid = currentUser.uid


        // First, try to find where `u_id` is `userId` and `f_id` is `friendId`
        friendsRef.whereEqualTo("u_id", uid).whereEqualTo("f_id", friendId).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Found the relationship document
                    callback(documents.documents[0].id)
                } else {
                    // Try the reverse case where `u_id` is `friendId` and `f_id` is `userId`
                    friendsRef.whereEqualTo("u_id", friendId).whereEqualTo("f_id", uid).get()
                        .addOnSuccessListener { reverseDocuments ->
                            if (!reverseDocuments.isEmpty) {
                                // Found the relationship document in the reverse order
                                callback(reverseDocuments.documents[0].id)
                            } else {
                                // No relationship document found
                                callback(null)
                            }
                        }
                        .addOnFailureListener { exception ->
                            exception.printStackTrace()
                            callback(null)
                        }
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                callback(null)
            }
    }

    fun sendMessage(
        friendId: String,
        content: String,
        callback: (Boolean) -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            callback(false)
            return
        }

        val uid = currentUser.uid



        val message = hashMapOf(
            "senderId" to uid,
            "content" to content,
            "timestamp" to Timestamp.now()
        )
        getFriendRelationshipId(friendId) { relationshipId ->
            if (relationshipId != null) {
                db.collection("friends")
                    .document(relationshipId)
                    .collection("messages")
                    .add(message)
                    .addOnSuccessListener {
                        callback(true)
                    }
                    .addOnFailureListener {
                        callback(false)
                    }
            } else {
                println("Friend relationship not found.")
            }
        }
    }

    fun getMessages(relationshipId: String, callback: (List<MessageData>?) -> Unit) {
        db.collection("friends")
            .document(relationshipId)
            .collection("messages")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                val messages = snapshot.documents.mapNotNull { document ->
                    document.toObject(MessageData::class.java)
                }
                callback(messages)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}