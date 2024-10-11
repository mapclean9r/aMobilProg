package com.example.mobprog.gui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EventView(navController: NavController, eventData: EventData?, currentEvent: EventData?) {
        val eventService = EventService()
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()

        print("This it current EVent: ${currentEvent?.id}")

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(88.dp)
                        .padding(bottom = 10.dp, top = 24.dp)
                        .background(MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Event",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (currentEvent != null) {
                        CoverImageAPIEvent(currentEvent.image)
                    }
                    if (currentEvent != null) {
                        Text(
                            text = currentEvent.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .padding(start = 28.dp, top = 12.dp, end = 28.dp)
                                .wrapContentHeight()

                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "Description: " + currentEvent.description,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp, bottom = 20.dp)
                                .wrapContentHeight()
                        )
                    }
                    Divider(color = Color.Gray, thickness = 1.dp)
                    if (currentEvent != null) {
                        Text(
                            text = "Price: " + currentEvent.price,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "Location: " + currentEvent.location,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "Date: " + currentEvent.startDate,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "Creator: " + currentEvent.host,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "People attending: " + currentEvent.attending.size,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "Maximum Party size: " + currentEvent.maxAttendance.toString(),
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    //Spacer(modifier = Modifier.height(500.dp).padding(paddingValues))
                    Button(
                        onClick = {
                            if (currentEvent != null) {
                                eventService.joinEvent(currentUserID, currentEvent.id)
                            }
                        },
                        modifier = Modifier
                            .padding(16.dp)

                    ) {
                        Text("Join")
                    }
                }
            }
        )
    }



@Composable
fun CoverImageAPIEvent(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Cover Image",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .padding(top = 25.dp)
            .fillMaxWidth()
            .height(200.dp)
            .width(12.dp)
    )
}





