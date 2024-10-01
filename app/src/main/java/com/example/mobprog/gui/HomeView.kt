package com.example.mobprog.gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.home.EventBox

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(navController: NavController, modifier: Modifier = Modifier) {

    val eventService = EventService()

    fun parseToEventData(data: Map<String, Any>): EventData? {
        println("Raw Data: $data")
        return try {
            EventData(
                name = data["name"] as? String ?: "",
                startDate = data["startDate"] as? String ?: "",
                creatorId = data["creatorId"] as? String ?: "",
                description = data["description"] as? String ?: "",
                price = data["price"] as? String ?: "Free",
                location = data["location"] as? String ?: "N/A",
                picture = data["picture"] as? String ?: "",
                host = data["host"] as? String ?: "N/A",
                date = data["date"] as? String ?: "N/A",
                comments = data["comments"] as? List<String> ?: emptyList(),
                attendance = data["attendance"] as? Int ?: 0,
                members = data["members"] as? List<String> ?: emptyList()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadEvents(onEventsLoaded: (List<EventData>) -> Unit) {
        eventService.getAllEvents { eventsList ->
            eventsList?.let { documents ->
                val eventDataList = documents.mapNotNull { document ->
                    parseToEventData(document)
                }

                // Pass the data back via the callback
                onEventsLoaded(eventDataList)
            } ?: run {
                onEventsLoaded(emptyList()) // If no events, return an empty list
            }
        }
    }
/*    val dummy = listOf(
            EventBase("League Lan", 8, ""),
            EventBase("Rocket League Fiesta", 12, ""),
            EventBase("Justice League Party", 1337, ""),
            ) */

    var events: List<EventData> = emptyList()

    /* eventService.getAllEvents { eventsList ->
        eventsList?.let { documents ->
            var eventDataList = documents.mapNotNull { document ->
                parseToEventData(document)
            }
            events = eventDataList

            for (event in eventDataList) {
                println("Event Name: ${event.name}")
                println("Event Date: ${event.startDate}")
                println("Creator: ${event.creatorId}")
                // Access other fields as needed
            }
        } ?: run {
            println("No events found")
        }
    } */

    println("@@@@@@@@@@" + events.size)


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
                    IconButton(onClick = {
                        eventService.getEventById("O47UxJAkXFd1jToaGnxt") { event ->
                            println(event)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Homepage",
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                loadEvents { eventDataList -> events = eventDataList }
                items(events) { event ->
                    EventBox(eventData = event)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            BottomNavBar(navController = navController)
        }
    )


}

@Preview(showBackground = true)
@Composable
fun HomeViewPreview() {
    HomeView(navController = rememberNavController())
}