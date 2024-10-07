package com.example.mobprog

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
import com.example.mobprog.data.EventService
import com.example.mobprog.data.GuildService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.CreateEventView
import com.example.mobprog.gui.FriendsView
import com.example.mobprog.gui.HomeView
import com.example.mobprog.gui.LoginView
import com.example.mobprog.gui.NotificationView
import com.example.mobprog.gui.ProfileView
import com.example.mobprog.gui.RegisterView
import com.example.mobprog.gui.SettingsView
import com.example.mobprog.gui.guild.CreateGuildView
import com.example.mobprog.gui.guild.GuildView
import com.example.mobprog.gui.guild.NoGuildView
import com.example.mobprog.ui.theme.MobProgTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Arena()
        }
    }
}

@Composable
fun Arena() {
    var isDarkMode by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    MobProgTheme(darkTheme = isDarkMode) {
        NavHost(navController = navController, startDestination = "loginScreen") {
            composable("loginScreen") {
                LoginView(navController = navController)
            }
            composable("registerScreen") {
                RegisterView(navController = navController)
            }
            composable("homeScreen") {
                HomeView(navController = navController, eventService = EventService())
            }
            composable("createEventScreen") {
                CreateEventView(navController = navController, eventService = EventService())
            }
            composable("friendsScreen") {
                FriendsView(navController = navController)
            }
            composable("guildScreen") {
                GuildView(navController = navController)
            }
            composable("createGuildScreen") {
                CreateGuildView(navController = navController, userService = UserService())
            }
            composable("noGuildView") {
                NoGuildView(navController = navController, guildService = GuildService())
            }
            composable("profileScreen") {
                ProfileView(navController = navController, userService = UserService())
            }
            composable("notificationScreen") {
                NotificationView(navController = navController)
            }
            composable("settingsScreen") {
                SettingsView(navController = navController, isDarkMode = isDarkMode, onDarkModeToggle = { isDarkMode = it})
            }
        }
    }
}