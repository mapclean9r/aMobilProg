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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.maps.EventLocationMapView
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

@Composable
fun EventView(navController: NavController, eventData: EventData?, currentEvent: EventData?) {
    val eventService = EventService()
    val userService = UserService()
    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()

    var username by remember { mutableStateOf("") }

    fun attenderStarterValue(): Int {
        return currentEvent?.attending?.size ?: 0
    }

    var numberOfPeopleAttending by remember { mutableIntStateOf(attenderStarterValue()) }

    fun updateAttendingPeople() {
        if (currentEvent != null) {
            if (!currentEvent.attending.contains(currentUserID)) {
                numberOfPeopleAttending = currentEvent.attending.size + 1
            }
        }
    }

    if (currentEvent != null) {
        userService.getUsernameWithDocID(currentEvent.creatorId) { creatorId ->
            username = creatorId ?: "username not found..."
        }
    }

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
                currentEvent?.let {
                    CoverImageAPIEvent(it.image)
                    Text(
                        text = it.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .padding(start = 28.dp, top = 12.dp, end = 28.dp)
                            .wrapContentHeight()
                    )
                    Text(
                        text = it.description,
                        modifier = Modifier
                            .padding(start = 28.dp, end = 28.dp, top = 8.dp, bottom = 20.dp)
                            .wrapContentHeight()
                    )
                    Divider(color = Color.Gray, thickness = 1.dp)
                    if (currentEvent != null) {
                        Text(
                            text = currentEvent.startDate + " - " + currentEvent.endDate,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "Host: $username",
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            //text = "People attending: " + currentEvent.attending.size,

                            text = "People attending: $numberOfPeopleAttending",
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            text = "Max Party size: " + currentEvent.maxAttendance.toString(),
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    // Location START
                    if (currentEvent != null) {
                        val coordinatesParts = currentEvent.coordinates.split(",")
                        if (coordinatesParts.size == 2) {
                            val latitude = coordinatesParts[0].toDoubleOrNull()
                            val longitude = coordinatesParts[1].toDoubleOrNull()

                            if (latitude != null && longitude != null) {
                                Text(
                                    text = "Location: ${currentEvent.location}",
                                    modifier = Modifier
                                        .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                        .wrapContentHeight()
                                )
                                EventLocationMapView(latitude, longitude)
                            } else {
                                Text(
                                    text = currentEvent.location.uppercase(Locale.ROOT),
                                    modifier = Modifier
                                        .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                        .wrapContentHeight()
                                )
                            }
                        } else {
                            Text(
                                text = currentEvent.location.uppercase(Locale.ROOT),
                                modifier = Modifier
                                    .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                    .wrapContentHeight()
                            )
                        }
                    }
                    // Location END
                    //Spacer(modifier = Modifier.height(500.dp).padding(paddingValues))
                    Row {
                        Button(
                            onClick = {
                                eventService.joinEvent(currentUserID, it.id)
                                updateAttendingPeople()
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Join")
                        }
                        ShowDeleteButton(it.creatorId, currentUserID, navController, eventService, it)
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController = navController, userService = UserService())
        }
    )
}

@Composable
fun ShowDeleteButton(
    userIFromDb: String,
    hostID: String,
    navController: NavController,
    eventService: EventService,
    currentEvent: EventData?
) {
    if (userIFromDb == hostID) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = {
                currentEvent?.let {
                    eventService.deleteEvent(it.id)
                    navController.navigate("homeScreen") {
                        navController.popBackStack()
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Delete")
        }
    }
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
    )
}
