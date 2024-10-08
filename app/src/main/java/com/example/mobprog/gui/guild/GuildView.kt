package com.example.mobprog.gui.guild

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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
import com.example.mobprog.data.GuildService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.guild.GuildData

@Composable
fun GuildView(navController: NavController, modifier: Modifier = Modifier, userService: UserService) {

    val guildDataState = remember { mutableStateOf<GuildData?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        GuildService().getCurrentUserGuildData { guildData ->
            guildDataState.value = guildData
            isLoading.value = false
        }
    }


    if (isLoading.value) {
        CircularProgressIndicator()
    } else {
        val guildData = guildDataState.value
        if (guildData != null) {
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
                            IconButton(
                                onClick = { /* TODO - åpne søkefelt */ },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    tint = Color.White,
                                )
                            }
                            Text(
                                text = "Guild",
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
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(62.dp) // The size of the circular container
                                .background(
                                    Color.LightGray,
                                    shape = CircleShape
                                ) // Background with circle shape and light grey color
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile icon",
                                tint = Color.Black,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Guild Name:  ${guildData.name}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.Start)
                            )
                            Text(
                                text = "Guild Leader:  ${guildData.leader}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.Start)
                            )
                            Text(
                                text = "Guild Members: " + "",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(320.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround) {
                                Button(onClick = { /*TODO*/ }) {
                                    Text(text = "Invite members")
                                }
                                Button(onClick = { /*TODO*/ }) {
                                    Text(text = "Create Guild Event")
                                }
                            }
                            Button(
                                onClick = { userService.updateUserGuild("") { success, exception ->
                                    if (success) {
                                        /* TODO: Lage notification for left guild */
                                        navController.navigate("noGuildScreen") {
                                            while (navController.popBackStack() == true) {
                                                navController.popBackStack()
                                            }
                                        }
                                    } else {
                                        /* TODO: Lage feilmelding til bruker hvis det ikke gikk å leave guild */
                                        exception?.printStackTrace()
                                    }
                                } },
                                colors = ButtonColors(Color.Red, Color.White, Color.Gray, Color.White),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.End)){
                                Text(text = "Leave Guild")
                            }


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
    }
}

@Preview(showBackground = true)
@Composable
fun GuildViewPreview() {
    GuildView(navController = rememberNavController(), userService = UserService())
}
