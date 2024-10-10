package com.example.mobprog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.api.GameData
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.GuildService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.CreateEventView
import com.example.mobprog.gui.friends.FriendsView
import com.example.mobprog.gui.HomeView
import com.example.mobprog.gui.LoginView
import com.example.mobprog.gui.NotificationView
import com.example.mobprog.gui.ProfileView
import com.example.mobprog.gui.RegisterView
import com.example.mobprog.gui.SettingsView
import com.example.mobprog.gui.event.EventView
import com.example.mobprog.gui.friends.AddFriendView
import com.example.mobprog.gui.guild.CreateGuildView
import com.example.mobprog.gui.guild.GuildView
import com.example.mobprog.gui.guild.NoGuildView
import com.example.mobprog.settings.SettingsManager
import com.example.mobprog.ui.theme.MobProgTheme
import com.google.gson.Gson


@SuppressLint("StaticFieldLeak")
lateinit var settingsManager: SettingsManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            settingsManager = SettingsManager(this)
            Arena(settingsManager.isDarkMode())
        }
    }
}

@Composable
fun Arena(darkMODE: Boolean) {
    var isDarkMode by remember { mutableStateOf(darkMODE) }
    val navController = rememberNavController()
    val eventService = EventService()
    var thisEvent by remember { mutableStateOf<EventData>(EventData()) }



    MobProgTheme(darkTheme = isDarkMode) {

        NavHost(navController = navController, startDestination = "loginScreen") {
            composable("loginScreen") {
                LoginView(navController = navController)
            }
            composable("registerScreen") {
                RegisterView(navController = navController)
            }
            composable("homeScreen") {
                HomeView(navController = navController, eventService = EventService(), onEventClick = { selectedEvent ->
                    thisEvent = selectedEvent
                })
            }
            composable("createEventScreen") {
                CreateEventView(navController = navController, eventService = EventService())
            }
            composable("eventScreen") {
                EventView(navController = navController, eventData = EventData(), currentEvent = thisEvent)
            }
            composable("friendsScreen") {
                FriendsView(navController = navController)
            }
            composable("guildScreen") {
                GuildView(navController = navController, userService = UserService())
            }
            composable("createGuildScreen") {
                CreateGuildView(navController = navController, userService = UserService())
            }
            composable("noGuildScreen") {
                NoGuildView(navController = navController, guildService = GuildService())
            }
            composable("profileScreen") {
                ProfileView(navController = navController, userService = UserService())
            }
            composable("notificationScreen") {
                NotificationView(navController = navController)
            }
            composable("settingsScreen") {
                SettingsView(navController = navController,
                    onDarkModeToggle = { darkMode ->
                        // Update the state and save the preference
                        isDarkMode = darkMode
                        settingsManager.saveDarkMode(darkMode)
                    },
                    currentSettingsDarkMode = isDarkMode)
        }
            composable("addFriendScreen") {
                AddFriendView(navController = navController)
            }
    }
}}