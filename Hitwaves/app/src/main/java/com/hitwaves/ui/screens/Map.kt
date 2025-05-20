package com.hitwaves.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.hitwaves.R
import com.hitwaves.ui.component.GoBack
import com.hitwaves.utils.LocationService
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage

@Composable
fun ConcertMap(navController: NavController){
    val ctx = LocalContext.current
    val locationService = remember { LocationService(ctx) }
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }

    var userLocation by remember { mutableStateOf<Point?>(null) }

    LaunchedEffect(Unit) {
        try {
            val coord = locationService.getLocation()
            coord?.let {
                userLocation = Point.fromLngLat(it.longitude, it.latitude)
            }
        } catch (e: Exception) {
            showLocationDisabledAlert = true
        }
    }


    val viewportState = rememberMapViewportState {
        userLocation.let {
            setCameraOptions {
                //zoom(10.0)
                //center(Point.fromLngLat(-98.0, 39.5))
                center(it)
                pitch(0.0)
                bearing(0.0)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState
        ) {
            val marker = rememberIconImage(key = R.drawable.custom_location_puck, painter = painterResource(R.drawable.custom_location_puck))
            // Insert a PointAnnotation composable function with the geographic coordinate to the content of MapboxMap composable function.
            userLocation?.let {
                PointAnnotation(it) {
                    iconImage = marker
                }
            }

//            MapEffect(Unit) { mapView ->
//                mapView.location.updateSettings {
//                    locationPuck = LocationPuck2D(
//                        topImage = ImageHolder.from(R.drawable.custom_location_puck),
//                        bearingImage = null,
//                        shadowImage = null,
//                    )
//                    enabled = true
//                    puckBearingEnabled = false
//                    pulsingEnabled = true
//                }
//                viewportState.transitionToFollowPuckState()
//            }
        }

        GoBack(navController)
    }
}

