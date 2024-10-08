package com.example.mobprog.gui.guild

import android.util.Log
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.mobprog.data.GuildService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.components.BottomNavBar
import com.example.mobprog.guild.GuildData

@Composable
fun CreateGuildView(navController: NavController, modifier: Modifier = Modifier, userService: UserService) {

    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var picture by remember { mutableStateOf("") }
    var members by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userService.getCurrentUserData { userData ->
            println(userData)
            if (userData != null) {
                val fetchedName = userData["name"] as? String
                username = fetchedName ?: ""
            } else {
                Log.w("UserData", "No user data found for current user")
            }
        }
    }

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
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = "Create Guild",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier.padding(12.dp)
                )
                Text(text = "Name",
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
                    label = { Text("Enter Guildname") },
                    placeholder = { Text("guildname") },
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

                Text(text = "Picture",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.Start))
                TextField(
                    value = picture,
                    onValueChange = { newText ->
                        picture = newText
                        /* TODO behandle input her */
                    },
                    label = { Text("Enter picture id") },
                    placeholder = { Text("picture id") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(22.dp))
                Button(
                    onClick = {
                        onSubmit(
                            name,
                            description,
                            picture = picture,
                            guildService = GuildService(),
                            leader = username,
                            userService = userService,
                            navController = navController
                        )
                        name = ""
                        description = ""
                        picture = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Guild")
                }

            }
        },
        bottomBar = {
            BottomNavBar(navController = navController, userService = UserService())
        }
    )

}

fun onSubmit(
    name: String,
    description: String,
    picture: String,
    guildService: GuildService,
    leader: String,
    userService: UserService,
    navController: NavController
) {
    val guildData = GuildData(name = name, description = description, leader = leader, picture = picture)
    guildService.createGuild(guildData) { guildId, exception ->
        if (exception != null) {
            Log.e("CreateGuild", "Failed to create guild", exception)
        } else if (guildId != null) {
            userService.updateUserGuild(guildId) { success, error ->
                if (success) {
                    navController.navigate("guildScreen")
                } else {
                    Log.e("UpdateUserGuild", "Failed to update user's guild field", error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateGuildViewPreview() {
    CreateGuildView(navController = rememberNavController(), userService = UserService())
}
