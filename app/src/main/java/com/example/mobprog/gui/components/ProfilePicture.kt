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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mobprog.R
import com.example.mobprog.data.handlers.ImageHandler

private val imgHandler = ImageHandler()

@Composable
fun GetSelfProfileImage() {
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
fun GetSelfProfileImageCircle(size: Int) {
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
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else if (error != null) {
        Image(
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(size.dp),
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    } else {
        Text("Loading...")
    }
}

@Composable
fun GetUserProfileImageCircle(userID: String, size: Int) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        imgHandler.getAnyUserProfileImageUrl(
            userID = userID,
            onSuccess = { url -> imageUrl = url },
            onFailure = { exception -> error = exception.message }
        )
    }

    if (imageUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "User Profile Image",
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else if (error != null) {
        Image(
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(56.dp),
            painter = painterResource(id = R.drawable.profile),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    } else {
        Text("Loading...")
    }
}