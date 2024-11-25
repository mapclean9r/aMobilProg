package com.example.mobprog.gui.event

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventView(navController: NavController, eventData: EventData?, currentEvent: EventData?) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val eventService = EventService()
    val userService = UserService()
    val currentUserID = FirebaseAuth.getInstance().currentUser?.uid.toString()

    var isAttending by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }

    fun attenderStarterValue(): Int {
        return currentEvent?.attending?.size ?: 0
    }

    var numberOfPeopleAttending by remember { mutableIntStateOf(attenderStarterValue()) }

    LaunchedEffect(currentUserID) {
        UserService().getAttendingEvents { attendingEvents ->
            if (currentEvent != null) {
                isAttending = attendingEvents?.any { it.id == currentEvent.id } == true
            }
        }
    }

    fun updateAttendingPeople(isJoining: Boolean) {
        if (currentEvent != null) {
            if (isJoining) {
                isAttending = true
                numberOfPeopleAttending++
            } else {
                isAttending = false
                numberOfPeopleAttending--
            }
        }
    }

    if (currentEvent != null) {
        userService.getUsernameWithDocID(currentEvent.creatorId) { creatorId ->
            username = creatorId ?: "username not found..."
        }
    }

    if (!isLandscape) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Event",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back Button",
                                tint = Color.White
                            )
                        }
                    },

                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            ,
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        currentEvent?.let {
                            CoverImageAPIEvent(it.image)

                            val dateFormatter =
                                DateTimeFormatter.ofPattern("d/M/yyyy", Locale.getDefault())
                            val parsedDate = LocalDate.parse(it.startDate, dateFormatter)

                            val formattedDate =
                                parsedDate.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy"))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = formattedDate,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Light,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                            ) {
                                Text(
                                    text = it.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(start = 28.dp, top = 1.dp, end = 28.dp)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "Arrangement av: ",
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .padding(start = 28.dp, top = 2.dp, bottom = 8.dp)
                                        .wrapContentHeight()
                                )
                                Text(
                                    text = username,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp,
                                    modifier = Modifier
                                        .padding(end = 28.dp, top = 2.dp, bottom = 8.dp)
                                        .wrapContentHeight()

                                )
                            }






                            Text(
                                text = it.description,
                                modifier = Modifier
                                    .padding(start = 28.dp, end = 28.dp, top = 8.dp, bottom = 20.dp)
                                    .wrapContentHeight()
                            )
                            Divider(color = Color.Gray, thickness = 1.dp)


                            val coordinatesParts = currentEvent.coordinates.split(",")
                            if (coordinatesParts.size == 2) {
                                val latitude = coordinatesParts[0].toDoubleOrNull()
                                val longitude = coordinatesParts[1].toDoubleOrNull()
                                if (latitude != null && longitude != null) {
                                    Text(
                                        text = currentEvent.location,
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
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (!isAttending && currentEvent?.creatorId != currentUserID) {
                                Button(
                                    onClick = {
                                        if (currentEvent != null) {
                                            eventService.joinEvent(currentUserID, currentEvent.id)
                                            updateAttendingPeople(true)
                                            UserService().addEventToAttend(
                                                currentUserID, currentEvent.id,
                                                onSuccess = { println("Event added to user") },
                                                onFailure = { exception -> println("Failed to add event to user: ${exception.message}") }
                                            )
                                        }
                                    },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text("Join")
                                }
                            }
                            if (isAttending && currentEvent?.creatorId != currentUserID) {
                                Button(
                                    onClick = {
                                        if (currentEvent != null) {
                                            eventService.leaveEvent(currentUserID, currentEvent.id)
                                            updateAttendingPeople(false)
                                            UserService().removeEventFromAttend(
                                                currentUserID, currentEvent.id,
                                                onSuccess = { println("Event removed from user") },
                                                onFailure = { exception -> println("Failed to remove event from user: ${exception.message}") }
                                            )
                                        }
                                    },
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text("Leave")
                                }
                            }

                            if (currentEvent != null) {
                                ShowDeleteButton(
                                    currentEvent.creatorId,
                                    currentUserID,
                                    navController,
                                    eventService,
                                    currentEvent
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
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Event",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            },
            content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        currentEvent?.let {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(end = 8.dp)
                                ) {
                                    CoverImageAPIEvent(it.image)
                                }

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
                                        text = "${currentEvent.startDate} - ${currentEvent.endDate}",
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
                                        text = "Max Party size: ${currentEvent.maxAttendance}",
                                        modifier = Modifier
                                            .padding(top = 4.dp)
                                            .wrapContentHeight()
                                    )
                                    val coordinatesParts =
                                        currentEvent.coordinates.split(",")
                                    if (coordinatesParts.size == 2) {
                                        val latitude = coordinatesParts[0].toDoubleOrNull()
                                        val longitude = coordinatesParts[1].toDoubleOrNull()

                                        if (latitude != null && longitude != null) {
                                            Text(
                                                text = currentEvent.location,
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .wrapContentHeight()
                                            )

                                        } else {
                                            Text(
                                                text = currentEvent.location.uppercase(
                                                    Locale.ROOT
                                                ),
                                                modifier = Modifier
                                                    .padding(top = 4.dp)
                                                    .wrapContentHeight()
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = currentEvent.location.uppercase(Locale.ROOT),
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .wrapContentHeight()
                                        )
                                    }
                                    Row {
                                        if (!isAttending) {
                                            Button(
                                                onClick = {
                                                    eventService.joinEvent(
                                                        currentUserID,
                                                        currentEvent.id
                                                    )
                                                    updateAttendingPeople(true)
                                                    UserService().addEventToAttend(
                                                        currentUserID, currentEvent.id,
                                                        onSuccess = { println("Event added to user") },
                                                        onFailure = { exception -> println("Failed to add event to user: ${exception.message}") }
                                                    )
                                                },
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text("Join Now")
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    eventService.leaveEvent(
                                                        currentUserID,
                                                        currentEvent.id
                                                    )
                                                    updateAttendingPeople(false)
                                                    UserService().removeEventFromAttend(
                                                        currentUserID, currentEvent.id,
                                                        onSuccess = { println("Event removed from user") },
                                                        onFailure = { exception -> println("Failed to remove event from user: ${exception.message}") }
                                                    )
                                                },
                                                modifier = Modifier.padding(16.dp)
                                            ) {
                                                Text("Leave Now")
                                            }
                                        }

                                        ShowDeleteButton(
                                            currentEvent.creatorId,
                                            currentUserID,
                                            navController,
                                            eventService,
                                            currentEvent
                                        )
                                    }

                                }
                            }
                        }
                    }

                    item {
                        currentEvent?.let {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val coordinatesParts = currentEvent.coordinates.split(",")
                                if (coordinatesParts.size == 2) {
                                    val latitude = coordinatesParts[0].toDoubleOrNull()
                                    val longitude = coordinatesParts[1].toDoubleOrNull()

                                    if (latitude != null && longitude != null) {
                                        EventLocationMapView(latitude, longitude)
                                    }
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
            .padding(top = 1.dp)
            .fillMaxWidth()
            .height(250.dp)
    )
}
