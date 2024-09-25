package com.example.mobprog.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mobprog.data.NavigationBarItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeActivity(navController: NavController, modifier: Modifier = Modifier) {

    val navigationBarItemsList = listOf(
        NavigationBarItem("Home", Icons.Default.Home),
        NavigationBarItem("Create Event", Icons.Default.Add),
        NavigationBarItem("Friends", Icons.Default.AccountBox),
        NavigationBarItem("Guild", Icons.Default.Share)
    )

    var selectedNavigationBarItem by remember {
        mutableIntStateOf(0);
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // inspirert av link under for å lage navbar.
            // https://www.youtube.com/watch?v=O9csfKW3dZ4
            NavigationBar {
                navigationBarItemsList.forEachIndexed {index, navigationBarItem ->
                    NavigationBarItem(
                        selected = selectedNavigationBarItem == index,
                        onClick = { selectedNavigationBarItem = index },
                        icon = { Icon(imageVector = navigationBarItem.icon, contentDescription = "Icon") },
                        label = { Text(navigationBarItem.label) })
                }
            }
        }
        ) {
        Text(text = "HomePage", fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }

}

@Preview(showBackground = true)
@Composable
fun HomeActivityPreview() {
    HomeActivity(navController = rememberNavController())
}
