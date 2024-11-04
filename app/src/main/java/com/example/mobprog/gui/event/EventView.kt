package com.example.mobprog.gui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun EventView(navController: NavController, eventData: EventData?, currentEvent: EventData?) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val thisCurrentEvent = currentEvent;
    val eventService = EventService()
    val userService = UserService()
    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()


    var username by remember { mutableStateOf("") }

    fun attenderStarterValue(): Int {
        return thisCurrentEvent?.attending?.size ?: 0
    }

    var numberOfPeopleAttending by remember { mutableIntStateOf(attenderStarterValue()) }

    fun updateAttendingPeople() {
        if (thisCurrentEvent != null) {
            if (!thisCurrentEvent.attending.contains(currentUserID)) {
                numberOfPeopleAttending = currentEvent.attending.size + 1
            }
        }
    }

    if (thisCurrentEvent != null) {
        userService.getUsernameWithDocID(thisCurrentEvent.creatorId) { creatorId ->
            username = creatorId ?: "username not found..."
        }
    }

    if (!isLandscape) {
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
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {

                        IconButton(onClick = {
                            UserService().getCurrentUserData { docFields ->
                                println("$docFields")
                            }
                        },
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            IconButton(onClick = {
                                navController.popBackStack()
                            },
                                modifier = Modifier.align(Alignment.CenterEnd)) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back Button",
                                    tint = Color.White
                                )
                            }
                        }
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        thisCurrentEvent?.let {
                            // Cover Image
                            CoverImageAPIEvent(it.image)

                            // Event Details
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
                            Text(
                                text = "${thisCurrentEvent.startDate} - ${thisCurrentEvent.endDate}",
                                modifier = Modifier
                                    .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                    .wrapContentHeight()
                            )
                            Text(
                                text = "Host: $username",
                                modifier = Modifier
                                    .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                    .wrapContentHeight()
                            )
                            Text(
                                text = "People attending: $numberOfPeopleAttending",
                                modifier = Modifier
                                    .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                    .wrapContentHeight()
                            )
                            Text(
                                text = "Max Party size: ${thisCurrentEvent.maxAttendance}",
                                modifier = Modifier
                                    .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                    .wrapContentHeight()
                            )

                            // Location
                            val coordinatesParts = thisCurrentEvent.coordinates.split(",")
                            if (coordinatesParts.size == 2) {
                                val latitude = coordinatesParts[0].toDoubleOrNull()
                                val longitude = coordinatesParts[1].toDoubleOrNull()
                                if (latitude != null && longitude != null) {
                                    Text(
                                        text = thisCurrentEvent.location,
                                        modifier = Modifier
                                            .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                            .wrapContentHeight()
                                    )
                                    EventLocationMapView(latitude, longitude)
                                } else {
                                    Text(
                                        text = thisCurrentEvent.location.uppercase(Locale.ROOT),
                                        modifier = Modifier
                                            .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                            .wrapContentHeight()
                                    )
                                }
                            } else {
                                Text(
                                    text = thisCurrentEvent.location.uppercase(Locale.ROOT),
                                    modifier = Modifier
                                        .padding(start = 28.dp, end = 28.dp, top = 8.dp)
                                        .wrapContentHeight()
                                )
                            }
                        }
                    }

                    item {
                        // Action Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    if (thisCurrentEvent != null) {
                                        eventService.joinEvent(currentUserID, thisCurrentEvent.id)
                                    }
                                    updateAttendingPeople()
                                    if (thisCurrentEvent != null) {
                                        UserService().addEventToAttend(
                                            currentUserID, thisCurrentEvent.id,
                                            onSuccess = { println("Event added to user") },
                                            onFailure = { exception -> println("Failed to add event to user: ${exception.message}") }
                                        )
                                    }
                                },
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text("Join")
                            }
                            if (thisCurrentEvent != null) {
                                ShowDeleteButton(
                                    thisCurrentEvent.creatorId,
                                    currentUserID,
                                    navController,
                                    eventService,
                                    thisCurrentEvent
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                BottomNavBar(navController = navController, userService = UserService())
            }
        )

    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(bottom = 10.dp, top = 24.dp)
                        .background(MaterialTheme.colorScheme.primary),

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Event",
                        fontSize = 24.sp, // Slightly larger font for landscape mode
                        fontWeight = FontWeight.Bold,

                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .align(Alignment.CenterVertically)
                    )
                }
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        thisCurrentEvent?.let {
                            // First Row: Image and Event Details
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // Cover Image
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(end = 8.dp)
                                ) {
                                    CoverImageAPIEvent(it.image)
                                }

                                // Event Details Column
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = it.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = it.description,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp)
                                            .wrapContentHeight()
                                    )
                                    Divider(color = Color.Gray, thickness = 1.dp)

                                    Text(
                                        text = "${thisCurrentEvent.startDate} - ${thisCurrentEvent.endDate}",
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .wrapContentHeight()
                                    )
                                    Text(
                                        text = "Host: $username",
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .wrapContentHeight()
                                    )
                                    Text(
                                        text = "People attending: $numberOfPeopleAttending",
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .wrapContentHeight()
                                    )
                                    Text(
                                        text = "Max Party size: ${thisCurrentEvent.maxAttendance}",
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .wrapContentHeight()
                                    )
                                    if (thisCurrentEvent != null) {
                                        val coordinatesParts =
                                            thisCurrentEvent.coordinates.split(",")
                                        if (coordinatesParts.size == 2) {
                                            val latitude = coordinatesParts[0].toDoubleOrNull()
                                            val longitude = coordinatesParts[1].toDoubleOrNull()

                                            if (latitude != null && longitude != null) {
                                                Text(
                                                    text = thisCurrentEvent.location,
                                                    modifier = Modifier
                                                        .padding(top = 4.dp)
                                                        .wrapContentHeight()
                                                )

                                            } else {
                                                Text(
                                                    text = thisCurrentEvent.location.uppercase(
                                                        Locale.ROOT
                                                    ),
                                                    modifier = Modifier
                                                        .padding(top = 4.dp)
                                                        .wrapContentHeight()
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = thisCurrentEvent.location.uppercase(Locale.ROOT),
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .wrapContentHeight()
                                            )
                                        }
                                    }
                                    Row {

                                    
                                    Button(
                                        onClick = {
                                            eventService.joinEvent(
                                                currentUserID,
                                                thisCurrentEvent.id
                                            )
                                            updateAttendingPeople()
                                            UserService().addEventToAttend(
                                                currentUserID, thisCurrentEvent.id,
                                                onSuccess = { println("Event added to user") },
                                                onFailure = { exception -> println("Failed to add event to user: ${exception.message}") }
                                            )
                                        },
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text("Join Now")
                                    }
                                    ShowDeleteButton(
                                        thisCurrentEvent.creatorId,
                                        currentUserID,
                                        navController,
                                        eventService,
                                        thisCurrentEvent
                                    )
                                }
                                }
                            }
                        }
                    }

                    item {
                        // Second Row: Map and Buttons
                        thisCurrentEvent?.let {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (thisCurrentEvent != null) {
                                    val coordinatesParts = thisCurrentEvent.coordinates.split(",")
                                    if (coordinatesParts.size == 2) {
                                        val latitude = coordinatesParts[0].toDoubleOrNull()
                                        val longitude = coordinatesParts[1].toDoubleOrNull()

                                        if (latitude != null && longitude != null) {
                                            EventLocationMapView(latitude, longitude)
                                        }
                                    }
                                    // Show delete button if the event belongs to the current user

                                }
                            }

                        }
                    }
                }
            },
            bottomBar = {
                BottomNavBar(navController = navController, userService = UserService())
            }
        )

    }
}

@Composable
fun ShowDeleteButton(
    userIFromDb: String,
    hostID: String,
    navController: NavController,
    eventService: EventService,
    thisCurrentEvent: EventData?
) {
    if (userIFromDb == hostID) {
        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            onClick = {
                thisCurrentEvent?.let {
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
