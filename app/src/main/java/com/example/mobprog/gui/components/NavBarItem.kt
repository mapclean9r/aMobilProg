package com.example.mobprog.gui.components

import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)