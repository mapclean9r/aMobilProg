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

@Composable
fun EventBox(eventBase: EventBase) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.lol), /* TODO: Må få til henting fra API*/
            contentDescription = eventBase.getName(),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = eventBase.getName(),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Free",
            fontSize = 16.sp,
            fontWeight = FontWeight.W400
        )
        Text(
            text = "Location: " + eventBase.getLocation(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = "Dato: " + eventBase.getStartDate(),
            fontSize = 13.sp,
            fontWeight = FontWeight.W300
        )
        Text(
            text = "Arrangør: " + eventBase.getHost(),
            fontSize = 13.sp,
            fontWeight = FontWeight.W300
        )
    }
}