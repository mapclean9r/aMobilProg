package com.example.mobprog.gui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.mobprog.R
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.UserService
import java.util.Locale

@Composable
fun EventBox(navController: NavController, eventData: EventData, eventClick: (EventData) -> Unit) {

    val userService = UserService()
    var username by remember { mutableStateOf("") }

    userService.getUsernameWithDocID(eventData.creatorId) { creatorId ->
        if (creatorId != null) {
            username = creatorId
        } else {
            println("username not found...")
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(color = MaterialTheme.colorScheme.onPrimary)

            .clickable {
                eventClick(eventData)
                navController.navigate("eventScreen")
            }
            .padding(16.dp)
    ) {
        Column{
            Box(modifier = Modifier
                .align(Alignment.CenterHorizontally)){
                CoverImageAPI(
                    url = eventData.image,
                    date = eventData.startDate,
                    attending = eventData.attending.size,
                    maxAttendance = eventData.maxAttendance
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = eventData.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = eventData.location.uppercase(Locale.ROOT),
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun DynamicImageSelector(imageName: String) {
    val imageResource = when (imageName) {
        "lol" -> R.drawable.lol
        "rocket" -> R.drawable.rocket
        "heart" -> R.drawable.heart
        "guild" -> R.drawable.guild
        else -> R.drawable.lol // A default image in case of an invalid name
    }

    Image(
        painter = painterResource(id = imageResource),
        contentDescription = imageName,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
    )
}

@Composable
fun CoverImageAPI(url: String, date: String, attending: Int, maxAttendance: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = url,
            contentDescription = "Cover Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        DateDisplay(
            date = date,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )

        AttendanceDisplay(
            attending = attending,
            maxAttendance = maxAttendance,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        )
    }
}

@Composable
fun DateDisplay(date: String, modifier: Modifier = Modifier) {
    val formattedDate = formatDate(date)
    Text(
        text = formattedDate,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

fun formatDate(date: String): String {
    val parts = date.split("/")
    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )
    return if (parts.size == 3) {
        val day = parts[0]
        val monthIndex = parts[1].toIntOrNull()?.minus(1)
        val month = if (monthIndex != null && monthIndex in 0..11) {
            monthNames[monthIndex]
        } else {
            "Invalid"
        }
        val year = parts[2]

        "$day $month $year"
    } else {
        "Invalid Date"
    }
}

@Composable
fun AttendanceDisplay(attending: Int, maxAttendance: Int, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.baseline_person_24),
            contentDescription = "Profile Icon",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$attending/$maxAttendance",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}