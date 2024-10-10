package com.example.mobprog.gui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.mobprog.createEvent.EventData
import com.google.gson.Gson

@Composable
fun EventView(navController: NavController, eventData: EventData?, currentEvent: EventData) {

    println("callback: $currentEvent")


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
                            text = currentEvent.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            },
            content = { paddingValues ->

                Box(modifier = Modifier) {
                    Modifier
                        .width(100.dp)
                        .padding(paddingValues)
                        CoverImageAPIEvent(currentEvent.image)
                }
                Box(modifier = Modifier) {
                    Modifier.align(alignment = Alignment.Center)
                    Text(
                        text = currentEvent.price,
                        modifier = Modifier
                            .padding(start = 28.dp, top = 320.dp)
                            .fillMaxSize()
                    )
                    Text(
                        text = currentEvent.description,
                        modifier = Modifier
                            .padding(start = 28.dp, top = 342.dp)
                            .fillMaxSize()
                    )
                    Text(
                        text = currentEvent.startDate,
                        modifier = Modifier
                            .padding(start = 28.dp, top = 364.dp)
                            .fillMaxSize()
                    )
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
            .padding(top = 100.dp)
            .fillMaxWidth()
            .height(200.dp)
            .width(12.dp)
    )
}





