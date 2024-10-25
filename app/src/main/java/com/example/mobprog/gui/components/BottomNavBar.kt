package com.example.mobprog.gui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.mobprog.R
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


    val iconColor = MaterialTheme.colorScheme.secondary
    val navigationBarItemsList = listOf(
        NavBarItem("Home", R.drawable.baseline_home_24, "homeScreen", iconColor),
        NavBarItem("Event", R.drawable.baseline_add_24, "createEventScreen", iconColor),
        NavBarItem("Friends", R.drawable.baseline_contacts_24, "friendsScreen", iconColor),
        NavBarItem("Guild", R.drawable.baseline_shield_24, "guildScreenTemp", iconColor,
            routeAlternatives = listOf("guildScreen", "noGuildScreen")
        ),
        NavBarItem("Profile", R.drawable.baseline_person_24, "profileScreen", iconColor)
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
                        painter = painterResource(id = navigationBarItem.icon),
                        contentDescription = navigationBarItem.label,
                        tint = navigationBarItem.color
                    )
                },
                label = { Text(navigationBarItem.label) }
            )
        }
    }
}