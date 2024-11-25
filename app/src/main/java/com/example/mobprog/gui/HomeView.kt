package com.example.mobprog.gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.gui.components.EventBox
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(navController: NavController, eventService: EventService, modifier: Modifier = Modifier, onEventClick: (EventData) -> Unit) {
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var events by remember { mutableStateOf(emptyList<EventData>()) }
    var showSearch by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var filteredEvents by remember { mutableStateOf(emptyList<EventData>()) }

    eventService.getAllEvents { eventsList ->
        eventsList?.let { documents ->
            val eventDataList = documents.mapNotNull { document ->
                eventService.parseToEventData(document)
            }
            events = eventDataList
        } ?: run {
            println("No events found")
        }
    }

    LaunchedEffect(searchText) {
        filteredEvents = events.filter { event ->
            event.name.contains(searchText, ignoreCase = true)
        }
    }

    if (!isLandscape) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),

            topBar = @androidx.compose.runtime.Composable {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Homepage",
                            style = MaterialTheme.typography.headlineMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
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
            },

                    content = { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    if (filteredEvents.isEmpty()) {
                        items(events) { event ->
                            EventBox(
                                navController = navController,
                                eventData = event,
                                eventClick = { selectedEvent ->
                                    onEventClick(selectedEvent)
                                })
                            Spacer(modifier = Modifier.height(22.dp))
                        }
                    }
                    items(filteredEvents) { event ->
                        EventBox(
                            navController = navController,
                            eventData = event,
                            eventClick = { selectedEvent ->
                                onEventClick(selectedEvent)
                            })
                    }
                }
            },
            bottomBar = {
                // inspirert av link under for å lage navbar.
                // https://www.youtube.com/watch?v=O9csfKW3dZ4
                BottomNavBar(navController = navController, userService = UserService())
            }
        )
    }
    else {
        Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Homepage",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showSearch = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
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
        },
        content = { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 200.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (filteredEvents.isEmpty()) {
                    items(events) { event ->
                        EventBox(
                            navController = navController,
                            eventData = event,
                            eventClick = { selectedEvent ->
                                onEventClick(selectedEvent)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    items(filteredEvents) { event ->
                        EventBox(
                            navController = navController,
                            eventData = event,
                            eventClick = { selectedEvent ->
                                onEventClick(selectedEvent)
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomNavBar(navController = navController, userService = UserService())
        }
    )
    }
    if (showSearch) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding()
                .clickable {
                    showSearch = false
                }
        )
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search...") },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(fraction = 0.09f)
                .padding(top = 24.dp),
            singleLine = true
        )
    }
}
