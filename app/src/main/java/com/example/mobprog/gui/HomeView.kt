package com.example.mobprog.gui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.createEvent.EventBase
import com.example.mobprog.createEvent.EventManager
import com.example.mobprog.data.NavigationBarItem
import com.example.mobprog.data.UserService
import com.example.mobprog.home.EventBox

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeActivity(navController: NavController, modifier: Modifier = Modifier) {

    val userService = UserService()

    val navigationBarItemsList = listOf(
        NavigationBarItem("Home", Icons.Default.Home, "homeScreen"),
        NavigationBarItem("Create Event", Icons.Default.Add, "createEventScreen"),
        NavigationBarItem("Friends", Icons.Default.AccountBox, "friendsScreen"),
        NavigationBarItem("Guild", Icons.Default.Share, "guildScreen")
    )

    var selectedNavigationBarItem by remember {
        mutableIntStateOf(0);
    }

    val dummy = listOf(
            EventBase("League Lan", 8, ""),
            EventBase("Rocket League Fiesta", 12, ""),
            EventBase("Justice League Party", 1337, ""),
            )

    var events = EventManager().getEvents().toList()

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
                IconButton(onClick = {
                    userService.getCurrentUserData { docFields ->
                        println("$docFields")
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
                    color = Color.White
                )
                IconButton(onClick = { navController.navigate("profileScreen") }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Icon",
                        tint = Color.White
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
                items(dummy) { event ->
                    EventBox(eventBase = event)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            NavigationBar {
                navigationBarItemsList.forEachIndexed {index, navigationBarItem ->
                    NavigationBarItem(
                        selected = selectedNavigationBarItem == index,
                        onClick = { navController.navigate(navigationBarItem.route)},
                        icon = { Icon(imageVector = navigationBarItem.icon, contentDescription = "Icon") },
                        label = { Text(navigationBarItem.label) })
                }
            }
        }
    )


}

@Preview(showBackground = true)
@Composable
fun HomeActivityPreview() {
    HomeActivity(navController = rememberNavController())
}