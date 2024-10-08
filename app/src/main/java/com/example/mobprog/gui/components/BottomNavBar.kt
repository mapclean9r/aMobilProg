package com.example.mobprog.gui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mobprog.data.UserService

@Composable
fun BottomNavBar(navController: NavController, userService: UserService) {
    var guild by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        userService.getCurrentUserGuild { guildValue ->
            guild = guildValue
            isLoading = false
        }
    }

    val navigationBarItemsList = listOf(
        NavBarItem("Home", Icons.Default.Home, "homeScreen"),
        NavBarItem("Event", Icons.Default.Add, "createEventScreen"),
        NavBarItem("Friends", Icons.Default.AccountBox, "friendsScreen"),
        NavBarItem(
            "Guild",
            Icons.Default.Share,
            "guildScreenTemp",
            routeAlternatives = listOf("guildScreen", "noGuildScreen")
        ),
        NavBarItem("Profile", Icons.Default.Person, "profileScreen")
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navigationBarItemsList.forEach { navigationBarItem ->
            val isSelected = if (navigationBarItem.routeAlternatives.isNotEmpty()) {
                currentDestination?.route in navigationBarItem.routeAlternatives
            } else {
                currentDestination?.route == navigationBarItem.route
            }
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (navigationBarItem.label == "Guild") {
                        if (isLoading) {
                            return@NavigationBarItem
                        }
                        if (guild.isNullOrEmpty()) {
                            navController.navigate("noGuildScreen") {
                                while (navController.popBackStack() == true) {
                                    navController.popBackStack()
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate("guildScreen") {
                                while (navController.popBackStack() == true) {
                                    navController.popBackStack()
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    } else {
                        if (!isSelected) {
                            navController.navigate(navigationBarItem.route) {
                                while (navController.popBackStack() == true) {
                                    navController.popBackStack()
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = navigationBarItem.icon,
                        contentDescription = navigationBarItem.label
                    )
                },
                label = { Text(navigationBarItem.label) }
            )
        }
    }
}