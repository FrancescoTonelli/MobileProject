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
import androidx.compose.ui.graphics.Color

fun goToMap(navController: NavHostController) {
    navController.navigate("map"){
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun init(): HomeViewModel{
    return HomeViewModel()
}

@Composable
fun Home(navController: NavHostController) {

    var query by rememberSaveable { mutableStateOf("") }
    val onQueryChange: (String) -> Unit = { query = it }

    val homeViewModel = remember { init() }
    val nearestEvents by homeViewModel.nearestState
    val isNearestLoading by homeViewModel.isLoadingNearest
    val popularEvents by homeViewModel.popularState
    val isPopularLoading by homeViewModel.isLoadingPopular


    var nearestShow : List<EventForCards> by remember { mutableStateOf(emptyList()) }
    var popularShow : List<EventForCards> by remember { mutableStateOf(emptyList()) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        homeViewModel.getNearest(0.0, 0.0)
        homeViewModel.getPopularEvents()
    }

    LaunchedEffect(nearestEvents) {
        if (nearestEvents.success && nearestEvents.data != null) {
            nearestShow = nearestEvents.data!!.map { event ->
                EventForCards(
                    contentId = event.id,
                    isTour = false,
                    backgroundImage = event.image.orEmpty(),
                    title = if(!event.tourTitle.isNullOrEmpty()) "${event.tourTitle} - ${event.title}" else event.title,
                    artistName = event.artist,
                    artistImage = event.artistImage.orEmpty(),
                    description = "${event.placeName} - ${event.distance}km",
                    date = event.date
                )
            }

        } else if (!nearestEvents.success && nearestEvents.errorMessage != null) {
            snackbarHostState.showSnackbar(nearestEvents.errorMessage!!)
        }
    }

    LaunchedEffect(popularEvents) {
        if (popularEvents.success && popularEvents.data != null) {
            popularShow = popularEvents.data!!.map { event ->
                EventForCards(
                    contentId = event.id,
                    isTour = event.isTour,
                    title = event.title,
                    backgroundImage = event.image,
                    artistName = event.artistName,
                    artistImage = event.artistImage,
                    description = if(event.isTour) "Tour - ${event.concertCount} shows" else event.placeName,
                    date = if(event.isTour) null else event.date
                )
            }

        } else if (!nearestEvents.success && nearestEvents.errorMessage != null) {
            snackbarHostState.showSnackbar(nearestEvents.errorMessage!!)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
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
            if (query != "") {
                item {
                    Title(title = "Stai cercando: $query")
                }
            }
            else {
                item {
                    Title(title = "Near you")
                }

                item {
                    ButtonWithIcons(
                        startIcon = ImageVector.vectorResource(R.drawable.map),
                        textBtn = "Open map",
                        endIcon = ImageVector.vectorResource(R.drawable.arrow),
                        onClickAction = { goToMap(navController)}
                    )
                }


                if(isNearestLoading) {
                    item{
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    if (nearestShow.isEmpty()) {
                        item {
                            Text(
                                text = "No near events found",
                                modifier = Modifier.padding(16.dp),
                                style = Typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    color = Secondary
                                )
                            )
                        }
                    }
                    else {
                        items(nearestShow) { event ->
                            EventCard(event = event, navController)
                        }
                    }

                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Title(title = "Top Artists")
                }

                if(isPopularLoading) {
                    item{
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    if (popularShow.isEmpty()) {
                        item {
                            Text(
                                text = "No popular events found",
                                modifier = Modifier.padding(16.dp),
                                style = Typography.bodyLarge.copy(
                                    fontSize = 14.sp,
                                    color = Secondary
                                )
                            )
                        }
                    }
                    else {
                        items(popularShow) { event ->
                            EventCard(event = event, navController)
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                }
            }
        }
    }
}

fun getSampleEvents(): List<EventForCards> {
    return listOf(
        EventForCards(
            contentId = 0,
            isTour = false,
            title = "Live@1984",
            backgroundImage = "https://imgs.search.brave.com/KHtTeWS6X-fASJer5nDjtHqs2FbqcUsXQLmib1PHIDM/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4y/LmFsYnVtb2Z0aGV5/ZWFyLm9yZy8zNzV4/MC9hbGJ1bS8yMDEz/OTUtMTk4NC5qcGc",
            artistName = "Salmo",
            artistImage = "https://imgs.search.brave.com/thO1WdUflttR7I5iep_ljBahOoFBh16ffn2hH4N3EPs/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pMS5z/bmRjZG4uY29tL2Fy/dHdvcmtzLXYxTjZu/dGNHT1c1bXdYTTEt/Z2tRc3pRLXQ1MDB4/NTAwLmpwZw",
            description = "Unipol, Bologna",
            date = null
        )
    )
}

fun getSampleArtist(): List<Artist>{
    return listOf()
}