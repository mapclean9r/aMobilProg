package com.example.mobprog.gui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobprog.R
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.UserService
import java.util.Locale

@Composable
fun EventBox(navController: NavController, eventData: EventData, eventClick: (EventData) -> Unit) {

    val userService = UserService()
    var username by remember { mutableStateOf("") }

    userService.getUsernameWithDocID(eventData.creatorId) { creatorId ->
        if (creatorId != null) {
            username = creatorId
        } else {
            println("username not found...")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
            .clickable {
                eventClick(eventData)
                navController.navigate("eventScreen")
            }
    ) {
        CoverImageAPI(eventData.image)

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = eventData.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = eventData.startDate + " - " + eventData.endDate,
            fontSize = 13.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = eventData.location.toUpperCase(Locale.ROOT),
            fontSize = 12.sp,
            fontWeight = FontWeight.W300
        )
    }
}

@Composable
fun DynamicImageSelector(imageName: String) {
    val imageResource = when (imageName) {
        "lol" -> R.drawable.lol
        "rocket" -> R.drawable.rocket
        "heart" -> R.drawable.heart
        "guild" -> R.drawable.guild
        else -> R.drawable.lol // A default image in case of an invalid name
    }

    Image(
        painter = painterResource(id = imageResource),
        contentDescription = imageName,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    )
}

@Composable
fun CoverImageAPI(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Cover Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    )
}