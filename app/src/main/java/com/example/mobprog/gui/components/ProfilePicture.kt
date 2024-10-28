package com.example.mobprog.gui.components

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.example.mobprog.data.handlers.ImageHandler

@Composable
fun getProfileImage() {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val imgHandler = ImageHandler()

    // Hent bilde-URL når komposisjonen lastes
    LaunchedEffect(Unit) {
        imgHandler.getUserProfileImageUrl(
            onSuccess = { url -> imageUrl = url },
            onFailure = { exception -> error = exception.message }
        )
    }

    if (imageUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "User Profile Image",
            modifier = Modifier,
            contentScale = ContentScale.Crop
        )
    } else if (error != null) {
        Text(text = "Error loading image: $error", color = Color.Red)
    } else {
        Text("Loading image...")
    }
}
