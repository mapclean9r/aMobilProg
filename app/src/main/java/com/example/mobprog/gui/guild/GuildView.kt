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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import com.example.mobprog.gui.components.GetGuildProfileImageCircle
import com.example.mobprog.guild.GuildData
import com.google.firebase.auth.FirebaseAuth
@Composable
fun GuildView(navController: NavController, modifier: Modifier = Modifier, userService: UserService) {

    val guildService = GuildService()
    val membersIds = remember { mutableListOf<String>() }
    val usernames = remember { mutableStateListOf<String>() }
    val guildDataState = remember { mutableStateOf<GuildData?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val hostNameString = remember { mutableStateOf<String?>("") }

    userService.getUsernameWithDocID(guildDataState.value?.leader) { username ->
        hostNameString.value = username ?: "Unknown"
    }

    LaunchedEffect(Unit) {
        guildService.getCurrentUserGuildData { guildData ->
            guildDataState.value = guildData
            isLoading.value = false

            guildData?.members?.let { ids ->
                membersIds.addAll(ids)
                ids.forEach { memberId ->
                    userService.getUsernameWithDocID(memberId) { username ->
                        usernames.add(username ?: "Unknown")
                    }
                }
            }
        }
    }

    if (isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                strokeWidth = 15.dp,
                modifier = Modifier.size(60.dp)
            )
        }
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
                                .size(162.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    shape = CircleShape
                                )
                                .padding(1.dp)
                        ) {
                            GetGuildProfileImageCircle(guildID = guildData.guildId, size = 250)
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
                                text = "Guild Leader: ${hostNameString.value}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.padding(12.dp)
                            )
                            Text(
                                text = "Guild Members:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.padding(12.dp)
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(usernames) { username ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .padding(12.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .padding(end = 8.dp)
                                            )
                                            Text(
                                                text = username,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            )
                                        }
                                    }
                                }
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red
                                ),
                                onClick = {
                                    userService.updateUserGuild("") { success, exception ->
                                        if (success) {
                                            navController.navigate("noGuildScreen") {
                                                while (navController.popBackStack()) {
                                                    navController.popBackStack()
                                                }
                                            }
                                        } else {
                                            exception?.printStackTrace()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.End)
                            ) {
                                Text(text = "Leave Guild")
                            }
                        }
                    }
                },
                bottomBar = {
                    BottomNavBar(navController = navController, userService = userService)
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

