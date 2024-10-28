package com.example.mobprog.data.handlers

import android.net.Uri
import com.example.mobprog.data.UserService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ImageHandler {

    fun uploadImageToFirebase(userImageUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
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

    fun getUserProfileImageUrl(onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid == null) {
            onFailure(Exception("User not logged in || user does not exist"))
            return
        }

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
}