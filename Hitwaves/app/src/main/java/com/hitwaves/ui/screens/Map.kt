package com.hitwaves.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.hitwaves.R
import com.hitwaves.api.MapConcertResponse
import com.hitwaves.api.getHttpArtistImageUrl
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.BgDark
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography
import com.hitwaves.ui.viewModel.LocationViewModel
import com.hitwaves.ui.viewModel.MapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import java.io.ByteArrayOutputStream
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.plugin.Plugin

private fun initLocation(): LocationViewModel {
    return LocationViewModel()
}

private fun initMap(): MapViewModel {
    return MapViewModel()
}

@Composable
fun ConcertMap(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val locationViewModel: LocationViewModel = remember { initLocation() }
    val mapViewModel: MapViewModel = remember { initMap() }

    val isGpsEnabled      by locationViewModel.isGpsEnabled.collectAsState()
    val isLoadingLocation by locationViewModel.isLoadingLocation
    val userLocation      by locationViewModel.locationState

    val mapState      by mapViewModel.mapEventState
    val isLoadingPOIs by mapViewModel.isLoadingPOIs

    val poiList = if (mapState.success && mapState.data != null) mapState.data!! else emptyList()
    val viewportState = rememberMapViewportState()

    DisposableEffect(Unit) {
        locationViewModel.registerGpsStatusReceiver(context)
        onDispose {
            locationViewModel.unregisterGpsStatusReceiver(context)
        }
    }

    LaunchedEffect(Unit) {
        mapViewModel.getPOIs()
        locationViewModel.getUserLocation(context)
    }

    LaunchedEffect(isGpsEnabled) {
        if (!isGpsEnabled) {
            snackbarHostState.showSnackbar("GPS disattivato — attivalo per vedere gli eventi vicino a te")
        } else {
            locationViewModel.getUserLocation(context)
        }
    }

    LaunchedEffect(isLoadingLocation, userLocation) {
        if (!isLoadingLocation && userLocation.first == null && userLocation.second == null) {
            locationViewModel.setIsGpsEnabled(false)
        }
        else if ( !isLoadingLocation && userLocation.first != null && userLocation.second != null) {
            val lat = userLocation.first!!
            val lon = userLocation.second!!

            viewportState.setCameraOptions(
                cameraOptions {
                    center(Point.fromLngLat(lon, lat))
                    zoom(13.1)
                }
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState

        ) {
            val marker = rememberIconImage(key = R.drawable.custom_location_puck, painter = painterResource(R.drawable.custom_location_puck))
            if (userLocation.first != null && userLocation.second != null) {
                val userPoint = Point.fromLngLat(userLocation.second!!, userLocation.first!!)
                PointAnnotation(userPoint) {
                    iconImage = marker
                }
            }
        }

        if (isLoadingPOIs || isLoadingLocation) {
            LoadingIndicator()
        }

    }

    CustomSnackbar(snackbarHostState)

    GoBack(navController)
}