package com.example.mobprog.gui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.mobprog.data.handlers.ImageHandler

private val imgHandler = ImageHandler()

@Composable
fun GetGuildProfileImage() {
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
            contentDescription = "Guild Profile Image",
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
fun GetGuildProfileImageCircle(guildID: String, size: Int) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        imgHandler.getGuildImageUrl(
            guildID = guildID,
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
        DynamicImageSelector(imageName = "guild")
    } else {
        Text("")
    }
}

@Composable
fun GetGuildProfileImageView(guildID: String, size: Int) {
    var imageUrl by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        imgHandler.getGuildImageUrl(
            guildID = guildID,
            onSuccess = { url -> imageUrl = url },
            onFailure = { exception -> error = exception.message }
        )
    }
    val widthSize = size/2
    if (imageUrl != null) {
        Image(
            painter = rememberAsyncImagePainter(imageUrl),
            contentDescription = "User Profile Image",
            modifier = Modifier
                .height(widthSize.dp)
                .width(size.dp)
                .clip(RectangleShape),
            contentScale = ContentScale.FillWidth
        )
    } else if (error != null) {
        DynamicImageSelector(imageName = "guild")
    } else {
        Text("Loading...")
    }
}