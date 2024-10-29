
package com.example.mobprog.maps

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun LocationPickerView(
    navController: NavController,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    isFineLocationGranted: Boolean,
    isCoarseLocationGranted: Boolean
) {
    val context = LocalContext.current

    // State to remember the selected LatLng position
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    if (isFineLocationGranted || isCoarseLocationGranted) {
        // Display Google Map if permissions are granted
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            onMapClick = { latLng ->
                // Update the state with the selected position
                selectedLatLng = latLng

                // When the map is clicked, call the callback with the selected location
                onLocationSelected(latLng.latitude, latLng.longitude)
                Toast.makeText(
                    context,
                    "Location selected: (${latLng.latitude}, ${latLng.longitude})",
                    Toast.LENGTH_SHORT
                ).show()
            },
            properties = MapProperties(isMyLocationEnabled = isFineLocationGranted),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ) {
            // Place a marker if a position has been selected
            selectedLatLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Selected Location"
                )
            }
        }
    } else {
        // Inform the user that permissions are required
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Location permissions are required to use this feature.",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    // Navigate back to request permissions again or show an error message
                    navController.popBackStack()
                }) {
                    Text("Grant Permissions")
                }
            }
        }
    }
}
