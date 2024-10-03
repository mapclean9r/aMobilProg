package com.example.mobprog.gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.gui.components.BottomNavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateEventView(navController: NavController, eventService: EventService) {

    /* TODO - legge til alle felter som trengs og endre tekst felter til å benytte disse */
    var name by remember { mutableStateOf("")}
    var location by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxAttendance by remember { mutableIntStateOf(0) }
    var maxAttendanceString by remember { mutableStateOf("") }

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
                Box(modifier = Modifier.fillMaxWidth()){
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
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "Create Event",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier.padding(12.dp)
                    )
                    Text(text = "Title",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start))
                    TextField(
                        value = name,
                        onValueChange = { newText ->
                            name = newText
                            /* TODO behandle input her */
                        },
                        label = { Text("Enter Title") },
                        placeholder = { Text("title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "Location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start))
                    TextField(
                        value = location,
                        onValueChange = { newText ->
                            location = newText
                            /* TODO behandle input her */
                        },
                        label = { Text("Enter Location") },
                        placeholder = { Text("location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "Price",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start))
                    TextField(
                        value = price,
                        onValueChange = { newText ->
                            price = newText
                        },
                        label = { Text("Enter Price") },
                        placeholder = { Text("price") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "Max Attendance",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start))
                    TextField(
                        value = maxAttendanceString,
                        onValueChange = { newText ->
                            maxAttendance = newText.toInt()
                        },
                        label = { Text("Enter Attendance") },
                        placeholder = { Text("attendance") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "Date",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start))
                    TextField(
                        value = startDate,
                        onValueChange = { newText ->
                            startDate = newText
                            /* TODO behandle input her */
                        },
                        label = { Text("Enter Date") },
                        placeholder = { Text("date") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "Description",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W400,
                        modifier = Modifier
                            .padding(6.dp)
                            .align(Alignment.Start))
                    TextField(
                        value = description,
                        onValueChange = { newText ->
                            description = newText
                            /* TODO behandle input her */
                        },
                        label = { Text("Enter Description") },
                        placeholder = { Text("description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    Button(
                        onClick = {
                            onSubmit(name, maxAttendance, price, location, startDate, description, eventService = eventService)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create Event")
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

fun onSubmit(name: String, maxAttendance: Int, price: String, location: String, startDate: String, description: String, eventService: EventService) {
    eventService.createEvent(EventData(name = name, maxAttendance = maxAttendance, location = location, description = description, startDate = startDate, price = price))
}

@Preview(showBackground = true)
@Composable
fun CreateEventViewPreview() {
    CreateEventView(navController = rememberNavController(), eventService = EventService())
}

