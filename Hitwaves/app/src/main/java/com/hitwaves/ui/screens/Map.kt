package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.hitwaves.R
import com.hitwaves.api.getHttpArtistImageUrl
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.viewModel.LocationViewModel
import com.hitwaves.ui.viewModel.MapViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import androidx.core.graphics.drawable.toBitmap
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.theme.BgDark
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationInteractionsState

private fun initLocation(): LocationViewModel {
    return LocationViewModel()
}

private fun initMap(): MapViewModel {
    return MapViewModel()
}

@Composable
fun ConcertMap(navController: NavController) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    val locationViewModel: LocationViewModel = remember { initLocation() }
    val mapViewModel: MapViewModel = remember { initMap() }

    val isGpsEnabled by locationViewModel.isGpsEnabled.collectAsState()
    val isLoadingLocation by locationViewModel.isLoadingLocation
    val userLocation by locationViewModel.locationState

    val mapState by mapViewModel.mapEventState
    val isLoadingPOIs by mapViewModel.isLoadingPOIs
    val poiList = mapState.data ?: emptyList()

    val viewportState = rememberMapViewportState()
    val imageSize = 180

    val debugText = remember { mutableStateOf("") }

    val defaultPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(getHttpArtistImageUrl(null))
            .transformations(CircleCropTransformation())
            .size(imageSize, imageSize)
            .allowHardware(false)
            .build()
    )

    val defaultConcertMarker = if (defaultPainter.state is AsyncImagePainter.State.Success) {
        rememberIconImage("default_concert_marker", defaultPainter)
    } else {
        null
    }

    val pointAnnotationInteractionsState = remember {
        PointAnnotationInteractionsState().onClicked { annotation ->
            val event = EventForCards(
                contentId = annotation.textField!!.toInt(),
                isTour = false,
                placeName = "",
                isTicket = false,
                title = "",
                backgroundImage = "",
                artistName = "",
                artistImage = "",
                description = "",
                date = ""
            )

            navController.currentBackStackEntry?.savedStateHandle?.set("event", event)

            navController.navigate("concertDetails")
            true
        }
    }

    DisposableEffect(Unit) {
        locationViewModel.registerGpsStatusReceiver(context)
        onDispose { locationViewModel.unregisterGpsStatusReceiver(context) }
    }

    LaunchedEffect(Unit) {
        mapViewModel.getPOIs()
        locationViewModel.getUserLocation(context)
    }

    LaunchedEffect(isGpsEnabled) {
        if (!isGpsEnabled) {
            snackBarHostState.showSnackbar("GPS disabled — enable it to see events near you")
        } else {
            locationViewModel.getUserLocation(context)
        }
    }

    LaunchedEffect(isLoadingLocation, userLocation) {
        val lat = userLocation.first
        val lon = userLocation.second
        if (!isLoadingLocation && lat == null && lon == null) {
            locationViewModel.setIsGpsEnabled(false)
        } else if (!isLoadingLocation && lat != null && lon != null) {
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
            val userMarker = rememberIconImage(
                key = R.drawable.custom_location_puck,
                painter = painterResource(R.drawable.custom_location_puck)
            )

            userLocation.first?.let { lat ->
                userLocation.second?.let { lon ->
                    PointAnnotation(Point.fromLngLat(lon, lat)) {
                        iconImage = userMarker
                    }
                }
            }

            val imageLoader = remember { ImageLoader(context) }

            poiList.forEach { concertPOI ->
                val point = Point.fromLngLat(concertPOI.longitude, concertPOI.latitude)
                val imageUrl = getHttpArtistImageUrl(concertPOI.artistImage?.takeIf { it.isNotEmpty() })

                val painterState = produceState<BitmapPainter?>(initialValue = null, imageUrl) {
                    val request = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .transformations(CircleCropTransformation())
                        .size(imageSize, imageSize)
                        .allowHardware(false)
                        .build()

                    val result = imageLoader.execute(request)
                    val drawable = result.drawable
                    value = drawable?.toBitmap(imageSize, imageSize)?.asImageBitmap()?.let { BitmapPainter(it) }
                }.value

                val markerImage = if (painterState != null) {
                    rememberIconImage(
                        key = "concert_${concertPOI.concertId}",
                        painter = painterState
                    )
                } else {
                    null
                }

                when {

                    markerImage != null -> {
                        PointAnnotation(
                            point = point
                        ) {
                            iconImage = markerImage
                            iconSize = 1.0
                            iconAnchor = IconAnchor.BOTTOM
                            interactionsState = pointAnnotationInteractionsState
                            textField = concertPOI.concertId.toString()
                            textColor = Color.Transparent
                        }
                    }

                    defaultConcertMarker != null -> {
                        PointAnnotation(
                            point = point
                        ) {
                            iconImage = defaultConcertMarker
                            iconSize = 1.0
                            iconAnchor = IconAnchor.BOTTOM
                            interactionsState = pointAnnotationInteractionsState
                            textField = concertPOI.concertId.toString()
                            textColor = Color.Transparent
                        }
                    }
                }
            }

            MapEffect { mapView ->
                try {
                    mapView.logo.enabled = false
                    mapView.scalebar.enabled = false
                } catch (_: Exception) {
                }
            }

        }

        if (isLoadingLocation || isLoadingPOIs) {
            LoadingIndicator()
        }
    }

    Text(
        text = debugText.value,
        modifier = Modifier
            .background(BgDark)
    )

    CustomSnackBar(snackBarHostState)
    GoBack(navController)
}
