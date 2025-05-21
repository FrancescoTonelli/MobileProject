package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.R
import com.hitwaves.ui.component.ButtonWithIcons
import com.hitwaves.ui.component.SearchWave
import com.hitwaves.ui.component.Title
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.EventCard
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.HomeViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import com.hitwaves.api.ApiResult
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.viewModel.LocationViewModel

fun goToMap(navController: NavHostController) {
    navController.navigate("map"){
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun initHome(): HomeViewModel{
    return HomeViewModel()
}

private fun initLocation(): LocationViewModel {
    return LocationViewModel()
}

@Composable
fun Home(navController: NavHostController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var query by rememberSaveable { mutableStateOf("") }
    val onQueryChange: (String) -> Unit = { query = it }

    val homeViewModel = remember { initHome() }
    val locationViewModel = remember { initLocation() }

    val location by locationViewModel.locationState
    val isLocationLoading by locationViewModel.isLoadingLocation
    val isGpsEnabled by locationViewModel.isGpsEnabled.collectAsState()

    val nearestEvents by homeViewModel.nearestState
    val isNearestLoading by homeViewModel.isLoadingNearest
    val popularEvents by homeViewModel.popularState
    val isPopularLoading by homeViewModel.isLoadingPopular

    var nearestShow by remember { mutableStateOf(emptyList<EventForCards>()) }
    var popularShow by remember { mutableStateOf(emptyList<EventForCards>()) }

    DisposableEffect(Unit) {
        locationViewModel.registerGpsStatusReceiver(context)
        onDispose { locationViewModel.unregisterGpsStatusReceiver(context) }
    }

    LaunchedEffect(Unit) {
        locationViewModel.getUserLocation(context)
        homeViewModel.getPopularEvents()
    }

    LaunchedEffect(location) {
        if(!isLocationLoading) {
            val (lat, lon) = location
            if (lat != null && lon != null) {
                homeViewModel.getNearest(lat, lon)
            }
            else {
                isGpsEnabled.and(false)
            }
        }
    }

    LaunchedEffect(isGpsEnabled) {
        if (!isGpsEnabled) {
            snackbarHostState.showSnackbar("GPS disabled — enable it to see events near you")
        } else {
            if (location.first == null || location.second == null) {
                locationViewModel.getUserLocation(context)
            }
        }
    }


    LaunchedEffect(nearestEvents) {
        if (!isNearestLoading) {
            nearestShow = if (nearestEvents.success && nearestEvents.data != null) {
                nearestEvents.data!!.map { event ->
                    EventForCards(
                        contentId = event.id,
                        isTour = false,
                        backgroundImage = event.image.orEmpty(),
                        title = if (!event.tourTitle.isNullOrEmpty()) "${event.tourTitle} - ${event.title}" else event.title,
                        artistName = event.artist,
                        artistImage = event.artistImage.orEmpty(),
                        description = "${event.placeName} - ${event.distance}km",
                        date = event.date,
                        placeName = event.placeName
                    )
                }
            } else emptyList()
        }
    }

    LaunchedEffect(popularEvents) {
        if (!isPopularLoading) {
            popularShow = if (popularEvents.success && popularEvents.data != null) {
                popularEvents.data!!.map { event ->
                    EventForCards(
                        contentId = event.id,
                        isTour = event.isTour,
                        title = event.title,
                        backgroundImage = event.image,
                        artistName = event.artistName,
                        artistImage = event.artistImage,
                        description = if (event.isTour) "Tour - ${event.concertCount} shows" else event.placeName,
                        date = if (event.isTour) null else event.date,
                        placeName = event.placeName
                    )
                }
            } else emptyList()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchWave(
            query = query,
            onQueryChange = onQueryChange,
            navController = navController
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Title(title = "Near you")
            }

            item {
                ButtonWithIcons(
                    startIcon = ImageVector.vectorResource(R.drawable.map),
                    textBtn = "Open map",
                    endIcon = ImageVector.vectorResource(R.drawable.arrow),
                    onClickAction = { goToMap(navController) }
                )
            }

            if (isLocationLoading || isNearestLoading) {
                item { CircularProgressIndicator(color = Color.White) }
            } else {
                if (nearestShow.isEmpty()) {
                    item {
                        Text(
                            text = "No near events found",
                            modifier = Modifier.padding(16.dp),
                            style = Typography.bodyLarge.copy(fontSize = 14.sp, color = Secondary)
                        )
                    }
                } else {
                    items(nearestShow) { event ->
                        EventCard(event = event, navController)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Title(title = "Top Artists")
            }

            if (isPopularLoading) {
                item {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                if (popularShow.isEmpty()) {
                    item {
                        Text(
                            text = "No popular events found",
                            modifier = Modifier.padding(16.dp),
                            style = Typography.bodyLarge.copy(fontSize = 14.sp, color = Secondary)
                        )
                    }
                } else {
                    items(popularShow) { event ->
                        EventCard(event = event, navController)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    CustomSnackbar(snackbarHostState)
}