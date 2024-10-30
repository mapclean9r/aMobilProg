package com.example.mobprog.maps

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun EventLocationMapView(latitude: Double, longitude: Double) {
    val mapCameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 15f)
    }
Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .height(200.dp),
    contentAlignment = Alignment.Center,
){
    GoogleMap(
        modifier = Modifier
            .width(300.dp)
            .height(200.dp),
        cameraPositionState = mapCameraPositionState,
        properties = MapProperties(mapType = MapType.NORMAL)
    ) {
        Marker(
            state = MarkerState(position = LatLng(latitude, longitude)),
            title = "Event Location"
        )
    }
}
}