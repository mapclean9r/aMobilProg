package com.example.mobprog.gui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mobprog.data.handlers.ImageHandler

val imgHandler = ImageHandler()

@Composable
fun GetProfileImage() {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

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

@Composable
fun GetProfileImageCircle() {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

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
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else if (error != null) {
        Text(text = "Error loading image: $error")
    } else {
        Text("Loading...")
    }
}
