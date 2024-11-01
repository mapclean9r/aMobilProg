package com.example.mobprog.gui.guild

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
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

@Composable
fun GuildView(navController: NavController, modifier: Modifier = Modifier, userService: UserService) {

    val guildService = GuildService()
    val membersMap = remember { mutableMapOf<String, String>() }
    val guildDataState = remember { mutableStateOf<GuildData?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val guildLeaderId = remember { mutableStateOf<String?>("") }
    var currentUserId  by remember { mutableStateOf("")}
    val promoteButtonVisibilityMap = remember { mutableStateMapOf<String, Boolean>() }
    var showChooseNewLeaderDialog by remember { mutableStateOf(false) }
    var selectedNewLeaderId by remember { mutableStateOf<String?>(null) }

    userService.getUsernameWithDocID(guildDataState.value?.leader) { leaderId ->
        guildLeaderId.value = leaderId ?: "Unknown"
    }

    LaunchedEffect(Unit) {
        userService.getCurrentUserData { userData ->
            if (userData != null) {
                val fetchedID = userData["id"] as? String
                currentUserId = fetchedID ?: ""
            } else {
                Log.w("UserData", "No user data found for current user")
            }
        }

        guildService.getCurrentUserGuildData { guildData ->
            guildDataState.value = guildData
            isLoading.value = false

            guildData?.members?.forEach { memberId ->
                userService.getUsernameWithDocID(memberId) { username ->
                    if (username != null) {
                        membersMap[memberId] = username
                        if (promoteButtonVisibilityMap[memberId] == null) {
                            promoteButtonVisibilityMap[memberId] = false
                        }
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
                                text = guildData.name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                            Row {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .padding(5.dp)
                                )
                                Text(
                                    text = "${guildLeaderId.value}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.padding(5.dp)
                                )
                            }

                            Text(
                                text = "Members:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.W400,
                                modifier = Modifier.padding(12.dp)
                            )
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                items(membersMap.toList()) { (memberId, username) ->ole@
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                            .padding(12.dp)
                                            .clickable(enabled = guildData.leader == currentUserId && guildData.leader != memberId) {
                                                promoteButtonVisibilityMap[memberId] = !(promoteButtonVisibilityMap[memberId] ?: false)
                                            }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
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
                                                modifier = Modifier.weight(1f)
                                            )

                                            if (promoteButtonVisibilityMap[memberId] == true) {
                                                Button(
                                                    onClick = {
                                                        guildService.transferGuildLeadership(
                                                            guildId = guildData.guildId,
                                                            currentLeaderId = currentUserId,
                                                            newLeaderId = memberId
                                                        ) { success, exception ->
                                                            if (success) {
                                                                promoteButtonVisibilityMap[memberId] = false
                                                                userService.getUsernameWithDocID(memberId) { newLeaderName ->
                                                                    guildDataState.value =
                                                                        guildDataState.value?.copy(
                                                                            leader = memberId,
                                                                            name = newLeaderName
                                                                                ?: "Unknown"
                                                                        )
                                                                }
                                                            } else {
                                                                exception?.printStackTrace()
                                                            }
                                                        }
                                                    },
                                                    modifier = Modifier.padding(start = 8.dp)
                                                ) {
                                                    Text(text = "Promote")
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Red
                                ),
                                onClick = {
                                    if (guildData.leader == currentUserId) {
                                        if (membersMap.size == 1) {
                                            userService.leaveGuild(navController, guildData.guildId, userService, guildService)
                                        } else {
                                            showChooseNewLeaderDialog = true
                                        }
                                    } else {
                                        userService.leaveGuild(navController, guildData.guildId, userService, guildService)
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

                    if (showChooseNewLeaderDialog) {
                        ChooseNewLeaderDialog(
                            membersMap = membersMap.filterKeys { it != currentUserId },
                            onSelectNewLeader = { newLeaderId ->
                                showChooseNewLeaderDialog = false
                                guildService.transferGuildLeadership(
                                    guildId = guildData.guildId,
                                    currentLeaderId = currentUserId,
                                    newLeaderId = newLeaderId
                                ) { success, exception ->
                                    if (success) {
                                        userService.leaveGuild(navController, guildData.guildId, userService, guildService)
                                    } else {
                                        exception?.printStackTrace()
                                    }
                                }
                            },
                            onDismiss = { showChooseNewLeaderDialog = false }
                        )
                    }
                },
                bottomBar = {
                    BottomNavBar(navController = navController, userService = userService)
                }
            )
        }
    }
}

@Composable
fun ChooseNewLeaderDialog(
    membersMap: Map<String, String>,
    onSelectNewLeader: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Choose a New Guild Leader") },
        text = {
            LazyColumn {
                items(membersMap.toList()) { (memberId, username) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectNewLeader(memberId) }
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = username, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GuildViewPreview() {
    GuildView(navController = rememberNavController(), userService = UserService())
}