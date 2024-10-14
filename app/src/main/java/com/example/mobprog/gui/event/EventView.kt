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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EventView(navController: NavController, eventData: EventData?, currentEvent: EventData?) {
        val eventService = EventService()
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()

    fun attenderStarterValue(): Int {
        if (currentEvent != null) {
            return currentEvent.attending.size
        }
        return 0
    }
        var numberOfPeopleAttending by remember { mutableIntStateOf(attenderStarterValue()) }


    fun updateAttendingPeople() {
        if (currentEvent != null) {
            numberOfPeopleAttending = currentEvent.attending.size
        }
    }


        val userService = UserService()
        var username by remember { mutableStateOf("") }

    if (currentEvent != null) {
        userService.getUsernameWithDocID(currentEvent.creatorId) { creatorId ->
            if (creatorId != null) {
                username = creatorId
            } else {
                println("username not found...")
            }
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
                    if (currentEvent != null) {
                        CoverImageAPIEvent(currentEvent.image)
                    }
                    if (currentEvent != null) {
                        println("This it current EVent: ${currentEvent}")
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
                            text = "Arrangør: " + username,
                            modifier = Modifier
                                .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                .wrapContentHeight()
                        )
                    }
                    if (currentEvent != null) {
                        Text(
                            //text = "People attending: " + currentEvent.attending.size,

                            text = "People attending: " + numberOfPeopleAttending,
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
                                updateAttendingPeople()
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





