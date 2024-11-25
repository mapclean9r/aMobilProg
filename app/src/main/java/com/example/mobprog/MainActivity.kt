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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobprog.createEvent.EventData
import com.example.mobprog.data.EventService
import com.example.mobprog.data.FriendService
import com.example.mobprog.data.GuildService
import com.example.mobprog.data.UserService
import com.example.mobprog.gui.AnyProfileView
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
import com.example.mobprog.gui.friends.FriendRequestView
import com.example.mobprog.gui.friends.message.FriendMessageView
import com.example.mobprog.gui.guild.CreateGuildView
import com.example.mobprog.gui.guild.GuildView
import com.example.mobprog.gui.guild.NoGuildView
import com.example.mobprog.maps.LocationPickerView
import com.example.mobprog.settings.SettingsManager
import com.example.mobprog.ui.theme.MobProgTheme
import com.example.mobprog.user.UserData
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import com.example.mobprog.gui.event.eventDataSaver
import com.example.mobprog.notifications.createNotificationChannels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
lateinit var settingsManager: SettingsManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize notification channels
        createNotificationChannels(this)
        
        // Initialize services with context
        val friendService = FriendService()
        friendService.setContext(this)
        
        val eventService = EventService()
        eventService.setContext(this)
        
        setContent {
            settingsManager = SettingsManager(this)
            Arena(settingsManager.isDarkMode(), eventService, friendService)
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun Arena(darkMODE: Boolean, eventService: EventService, friendService: FriendService) {
    var thisEvent by rememberSaveable(stateSaver = eventDataSaver) {
        mutableStateOf(EventData())
    }
    var isDarkMode by remember { mutableStateOf(darkMODE) }
    val navController = rememberNavController()
    var selectedUser by remember { mutableStateOf(UserData()) }
    val context = LocalContext.current
    var isFineLocationGranted by remember { mutableStateOf(false) }
    var isCoarseLocationGranted by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        isCoarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            println("Notification permission granted")
            eventService.checkTodayEvents()
        } else {
            println("Notification permission denied")
        }
    }

    LaunchedEffect(Unit) {
        launch(Dispatchers.IO) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }

        launch(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val isPermissionGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                if (!isPermissionGranted) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    eventService.checkTodayEvents()
                }
            }
        }
    }

    MobProgTheme(darkTheme = isDarkMode) {
        NavHost(
            navController = navController,
            startDestination = "loginScreen",
            enterTransition = {
                EnterTransition.None
            },
            exitTransition = {
                ExitTransition.None
            }
        ) {
            composable("loginScreen") {
                LoginView(navController = navController, isDarkMode = isDarkMode)
            }
            composable("registerScreen") {
                RegisterView(navController = navController, isDarkMode = isDarkMode)
            }
            composable("homeScreen") {
                LaunchedEffect(Unit) {
                    eventService.checkTodayEvents()
                }
                HomeView(
                    navController = navController,
                    eventService = eventService,
                    onEventClick = { selectedEvent ->
                        thisEvent = selectedEvent
                    })
            }
            composable("createEventScreen") {
                CreateEventView(navController = navController, eventService = eventService)
            }
            composable("locationPickerScreen") {
                LocationPickerView(
                    navController = navController,
                    onLocationSelected = { latitude, longitude, locationName ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_location", Triple(latitude, longitude, locationName)
                        )
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
                FriendsView(navController = navController, friendService = friendService)
            }
            composable("friendRequestScreen") {
                FriendRequestView(navController = navController, friendService = friendService)
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
                ProfileView(navController = navController, userService = UserService(), onEventClick = { selectedEvent ->
                    thisEvent = selectedEvent
                })
            }
            composable("notificationScreen") {
                NotificationView(navController = navController)
            }
            composable("settingsScreen") {
                SettingsView(
                    navController = navController,
                    onDarkModeToggle = { darkMode ->
                        isDarkMode = darkMode
                        settingsManager.saveDarkMode(darkMode)
                    },
                    currentSettingsDarkMode = isDarkMode
                )
            }
            composable("addFriendScreen") {
                AddFriendView(navController = navController, friendService = friendService)
            }
            composable(route = "friendMessageView/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                FriendMessageView(navController = navController, friendId = userId, friendService = friendService)
            }
            composable(
                route = "anyProfileView/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""

                // Use remember and mutableStateOf to hold the user data
                val userDataState = remember { mutableStateOf<UserData?>(null) }

                // Fetch the user data asynchronously
                LaunchedEffect(userId) {
                    UserService().getUserDataByID(userId) { userData ->
                        userDataState.value = userData  // Update the state with the fetched user data
                    }
                }

                // Pass the user data to AnyProfileView when it is available
                userDataState.value?.let { user ->
                    AnyProfileView(navController = navController, user = user)
                }
            }
        }
    }
}
