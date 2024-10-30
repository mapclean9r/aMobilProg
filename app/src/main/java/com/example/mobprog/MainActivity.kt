package com.example.mobprog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.example.mobprog.gui.friends.FriendInfoView
import com.example.mobprog.gui.friends.FriendRequestView
import com.example.mobprog.gui.guild.CreateGuildView
import com.example.mobprog.gui.guild.GuildView
import com.example.mobprog.gui.guild.NoGuildView
import com.example.mobprog.maps.LocationPickerView
import com.example.mobprog.settings.SettingsManager
import com.example.mobprog.ui.theme.MobProgTheme
import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    var thisEvent by remember { mutableStateOf(EventData()) }

    val context = LocalContext.current
    var isFineLocationGranted by remember { mutableStateOf(false) }
    var isCoarseLocationGranted by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        isCoarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (!isFineLocationGranted && !isCoarseLocationGranted) {
            // If neither permission is granted, inform the user
            // In a real-world app, you could use a snackbar, dialog, or similar UI element
            println("Location permission is required to access map features.")
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    MobProgTheme(darkTheme = isDarkMode) {

        NavHost(navController = navController,
            startDestination = "loginScreen",
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            composable("loginScreen") {
                LoginView(navController = navController)
            }
            composable("registerScreen") {
                RegisterView(navController = navController)
            }
            composable("homeScreen") {
                HomeView(
                    navController = navController,
                    eventService = EventService(),
                    onEventClick = { selectedEvent ->
                        thisEvent = selectedEvent
                    })
            }
            composable("createEventScreen") {
                CreateEventView(navController = navController, eventService = EventService())
            }
            composable("locationPickerScreen") {
                LocationPickerView(
                    navController = navController,
                    onLocationSelected = { latitude, longitude ->
                        navController.previousBackStackEntry?.savedStateHandle?.set("selected_location", Pair(latitude, longitude))
                    },
                    isFineLocationGranted = isFineLocationGranted,
                    isCoarseLocationGranted = isCoarseLocationGranted
                )
            }
            composable("eventScreen") {
                EventView(
                    navController = navController,
                    eventData = EventData(),
                    currentEvent = thisEvent
                )
            }
            composable("friendsScreen") {
                FriendsView(navController = navController)
            }
            composable("friendRequestScreen") {
                FriendRequestView(navController = navController)
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
                SettingsView(
                    navController = navController,
                    onDarkModeToggle = { darkMode ->
                        // Update the state and save the preference
                        isDarkMode = darkMode
                        settingsManager.saveDarkMode(darkMode)
                    },
                    currentSettingsDarkMode = isDarkMode
                )
            }
            composable("addFriendScreen") {
                AddFriendView(navController = navController)
            }
        }
    }
}