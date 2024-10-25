package com.example.mobprog.gui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class NavBarItem(
    val label: String,
    val icon: Int,
    val route: String,
    var color: Color,
    val routeAlternatives: List<String> = emptyList()
)