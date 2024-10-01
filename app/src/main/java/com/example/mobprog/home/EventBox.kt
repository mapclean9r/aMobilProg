package com.example.mobprog.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobprog.R
import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.createEvent.EventData

@Composable
fun EventBox(eventData: EventData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.lol), /* TODO: Må få til henting fra API*/
            contentDescription = eventData.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Name: " + eventData.name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = eventData.price,
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
        Text(
            text = "Location: " + eventData.location,
            fontSize = 12.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = "Dato: " + eventData.startDate,
            fontSize = 13.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "Arrangør: " + eventData.host,
            fontSize = 13.sp,
            fontWeight = FontWeight.W300
        )
    }
}