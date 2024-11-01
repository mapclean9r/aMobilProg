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
    onLocationSelected: (latitude: Double, longitude: Double, locationName: String) -> Unit,
    isFineLocationGranted: Boolean,
    isCoarseLocationGranted: Boolean
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current // Used to manage the focus state

    // Default: Oslo, Norway
    val defaultLocation = LatLng(59.9139, 10.7522)
    val defaultZoom = 10f

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, defaultZoom)
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var searchText by remember { mutableStateOf("") }
    var selectedLocationName by remember { mutableStateOf("") }

    val geocodingApiKey = "AIzaSyBxZif_OnF3EoynMVcZfwXTZrauOBrfScU" // Replace with your actual API key

    val retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val geocodingApiService = retrofit.create(GeocodingApiService::class.java)

    fun reverseGeocode(lat: Double, lng: Double) {
        val call = geocodingApiService.getLocationName("${lat},${lng}", geocodingApiKey)
        call.enqueue(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                if (response.isSuccessful) {
                    val locationName = response.body()?.results?.firstOrNull()?.formatted_address ?: "Unknown Location"
                    selectedLocationName = locationName
                } else {
                    Log.e("LocationPickerView", "Error fetching location name: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                Log.e("LocationPickerView", "Error: ${t.message}")
            }
        })
    }

    if (isFineLocationGranted || isCoarseLocationGranted) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
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
                                            focusManager.clearFocus()
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

                GoogleMap(
                    modifier = Modifier.weight(1f),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        selectedLatLng = latLng
                        reverseGeocode(latLng.latitude, latLng.longitude)
                        focusManager.clearFocus()
                    },
                    properties = MapProperties(isMyLocationEnabled = isFineLocationGranted),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    selectedLatLng?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Selected Location"
                        )
                    }
                }
            }

            selectedLatLng?.let {
                FloatingActionButton(
                    onClick = {
                        onLocationSelected(it.latitude, it.longitude, selectedLocationName)
                        navController.popBackStack() // Optionally navigate back after selecting the location
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp, 16.dp, 16.dp, 58.dp)
                ) {
                    Text("✓")
                }
            }
        }
    } else {
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
                    navController.popBackStack()
                }) {
                    Text("Grant Permissions")
                }
            }
        }
    }
}