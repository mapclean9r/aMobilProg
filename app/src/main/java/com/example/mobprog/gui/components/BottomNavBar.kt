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
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {
    val navigationBarItemsList = listOf(
        NavBarItem("Home", Icons.Default.Home, "homeScreen"),
        NavBarItem("Event", Icons.Default.Add, "createEventScreen"),
        NavBarItem("Friends", Icons.Default.AccountBox, "friendsScreen"),
        NavBarItem("Guild", Icons.Default.Share, "guildScreen"),
        NavBarItem("Profile", Icons.Default.Person, "profileScreen")
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        navigationBarItemsList.forEach { navigationBarItem ->
            val isSelected = currentDestination?.route == navigationBarItem.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(navigationBarItem.route) {
                            launchSingleTop = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            restoreState = true
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