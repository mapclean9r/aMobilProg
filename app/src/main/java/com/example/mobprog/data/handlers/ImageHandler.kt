package com.example.mobprog.data.handlers

import android.content.Context
import android.net.Uri
import com.example.mobprog.z_Old_Code.Guild
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class ImageHandler {

    fun uploadProfileImageToFirebase(userImageUri: Uri,
                                     onSuccess: () -> Unit,
                                     onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid == null) {
            onFailure(Exception("User not logged in || user does not exist"))
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference
            .child("users/$uid/profile.jpg")

        storageRef.putFile(userImageUri)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getUserProfileImageUrl(
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit)
    {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val storageRef = FirebaseStorage.getInstance().reference
            .child("users/$uid/profile.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getAnyUserProfileImageUrl(
        userID: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit)
    {

        val storageRef = FirebaseStorage.getInstance().reference
            .child("users/$userID/profile.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun uploadGuildImageToFirebase(userImageUri: Uri, guildID: String,
                                   onSuccess: () -> Unit,
                                   onFailure: (Exception) -> Unit)
    {

        val storageRef = FirebaseStorage.getInstance().reference
            .child("guilds/$guildID/guildProfile.jpg")

        storageRef.putFile(userImageUri)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getGuildImageUrl(guildID: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit)
    {
        val storageRef = FirebaseStorage.getInstance().reference
            .child("guilds/$guildID/guildProfile.jpg")

        storageRef.downloadUrl
            .addOnSuccessListener { uri ->
                onSuccess(uri.toString())
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun uploadDefaultProfilePicture(context: Context, userID: String) {
        val drawableUri = Uri.parse("android.resource://${context.packageName}/drawable/profile")

        val storageRef = FirebaseStorage.getInstance().reference
            .child("users/$userID/profile.jpg")
        storageRef.putFile(drawableUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    println("Download : $downloadUri")

                }.addOnFailureListener { e ->
                    println("Error: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                println("Error: ${e.message}")
            }
    }

}