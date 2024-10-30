package com.example.mobprog.maps

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun LocationPickerView(
    navController: NavController,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    isFineLocationGranted: Boolean,
    isCoarseLocationGranted: Boolean
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current // Used to manage the focus state

    // Default location: Oslo, Norway
    val defaultLocation = LatLng(59.9139, 10.7522)
    val defaultZoom = 10f

    // Camera position state to set default location and zoom
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, defaultZoom)
    }

    // State to remember the selected LatLng position
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var searchText by remember { mutableStateOf("") }

    val geocodingApiKey = "AIzaSyBxZif_OnF3EoynMVcZfwXTZrauOBrfScU" // Replace with your actual API key

    // Create Retrofit instance
    val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val geocodingApiService = retrofit.create(GeocodingApiService::class.java)

    if (isFineLocationGranted || isCoarseLocationGranted) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search bar
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    label = { Text("Search Location") },
                    placeholder = { Text("Enter a location") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    singleLine = true
                )

                Button(
                    onClick = {
                        // Make a geocoding request to search for the location
                        if (searchText.isNotBlank()) {
                            val call = geocodingApiService.getCoordinates(searchText, geocodingApiKey)
                            call.enqueue(object : Callback<GeocodingResponse> {
                                override fun onResponse(
                                    call: Call<GeocodingResponse>,
                                    response: Response<GeocodingResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val location = response.body()?.results?.firstOrNull()?.geometry?.location
                                        if (location != null) {
                                            val newLatLng = LatLng(location.lat, location.lng)
                                            cameraPositionState.position = CameraPosition.fromLatLngZoom(newLatLng, 12f)
                                            focusManager.clearFocus() // Clear the focus after the search
                                        }
                                    } else {
                                        Log.e("LocationPickerView", "Error fetching location: ${response.errorBody()?.string()}")
                                    }
                                }

                                override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                                    Log.e("LocationPickerView", "Error: ${t.message}")
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text("Search")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Google Map
                GoogleMap(
                    modifier = Modifier.weight(1f),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        // Update the state with the selected position
                        selectedLatLng = latLng

                        // Clear focus after clicking on the map
                        focusManager.clearFocus()
                    },
                    properties = MapProperties(
                        isMyLocationEnabled = isFineLocationGranted,
                        isTrafficEnabled = false,
                        isBuildingEnabled = false,
                        isIndoorEnabled = false),
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
            }

            // Floating Action Button to confirm the selected location
            selectedLatLng?.let {
                FloatingActionButton(
                    onClick = {
                        onLocationSelected(it.latitude, it.longitude)
                        navController.popBackStack() // Optionally navigate back after selecting the location
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text("✓")
                }
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