package com.example.mobprog.maps

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mobprog.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

@SuppressLint("MissingPermission") // Handle permissions explicitly
@Composable
fun LocationPickerView(
    navController: NavController,
    onLocationSelected: (latitude: Double, longitude: Double, locationName: String) -> Unit,
    isFineLocationGranted: Boolean,
    isCoarseLocationGranted: Boolean
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val geocodingApiKey = "AIzaSyBxZif_OnF3EoynMVcZfwXTZrauOBrfScU" // Replace with your API key
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    val defaultLocation = LatLng(59.9139, 10.7522) // Default: Oslo, Norway
    val defaultZoom = 10f

    // Initialize camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, defaultZoom)
    }

    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedLocationName by remember { mutableStateOf("") }

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

    fun performSearch(query: String) {
        val call = geocodingApiService.getCoordinates(query, geocodingApiKey)
        call.enqueue(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
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

    // Get user's current location if permissions are granted
    LaunchedEffect(isFineLocationGranted, isCoarseLocationGranted) {
        if (isFineLocationGranted || isCoarseLocationGranted) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    userLocation = currentLatLng
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 12f)
                }
            }.addOnFailureListener {
                Log.e("LocationPickerView", "Failed to get location: ${it.message}")
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding() ) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLatLng = latLng
                reverseGeocode(latLng.latitude, latLng.longitude)
                focusManager.clearFocus()
            },
            properties = MapProperties(isMyLocationEnabled = false), // My Location handled by custom button
            uiSettings = MapUiSettings(zoomControlsEnabled = false) // Zoom handled by custom buttons
        ) {
            selectedLatLng?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "Selected Location"
                )
            }
        }

        // Styled Search Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search here") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_location_24), // Replace with your location icon
                        contentDescription = "Location Icon"
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        performSearch(searchText.text)
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Gray.copy(alpha = 0.5f),
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
            )
        }
        // Custom Current Location Button
        FloatingActionButton(
            onClick = {
                userLocation?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 12f)
                }
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 100.dp) // Position below search field
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_my_location_24), // Replace with your location icon
                contentDescription = "My Location"
            )
        }
        // Custom Zoom Buttons
        Column(
            modifier = Modifier

                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 70.dp) // Align with FAB height
        ) {
            FloatingActionButton(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        cameraPositionState.position.target,
                        cameraPositionState.position.zoom + 1
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Text("+",
                    style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                ))
            }

            Spacer(modifier = Modifier.height(6.dp))

            FloatingActionButton(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        cameraPositionState.position.target,
                        cameraPositionState.position.zoom - 1
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Text("-",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    ))
            }
        }

        // Confirm Selection Button
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
                    .padding(16.dp, 16.dp, 16.dp, 66.dp)
            ) {
                Text("✓",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    ))
            }
        }
    }
}
