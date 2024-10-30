package com.example.mobprog.gui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
fun ProfileEventBox(navController: NavController, eventData: EventData, eventClick: (EventData) -> Unit) {

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
                .padding(12.dp)
                .clickable {
                    eventClick(eventData)
                    navController.navigate("eventScreen")
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GetCoverImageAPI(eventData.image)
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, // Sentraliserer elementene horisontalt
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = eventData.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = eventData.startDate + " - " + eventData.endDate,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W300
                    )
                    Text(
                        text = eventData.location.toUpperCase(Locale.ROOT),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.W300
                    )

                }


            }

        }

        Divider(color = Color.LightGray, thickness = 1.dp)

}


@Composable
fun GetCoverImageAPI(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Cover Image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .width(140.dp)
    )
}