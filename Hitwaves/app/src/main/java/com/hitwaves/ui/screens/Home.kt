package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.R
import com.hitwaves.component.ButtonWithIcons
import com.hitwaves.component.EventCard
import com.hitwaves.component.SearchWave
import com.hitwaves.component.Title
import com.hitwaves.model.Artist
import com.hitwaves.model.Event

fun onClick(navController: NavHostController) {
    navController.navigate("map"){
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}


@Composable
fun Home(navController: NavHostController) {
    val eventList = getSampleEvents()

    var query by rememberSaveable { mutableStateOf("") }
    val onQueryChange: (String) -> Unit = { query = it }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchWave(
            query = query,
            onQueryChange = onQueryChange,
            searchResultsArtists = getSampleArtist(),
            searchResultsEvents = getSampleEvents(),
            navController = navController
        )

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
                    onClickAction = { onClick(navController) }
                )
            }

            items(eventList) { event ->
                EventCard(event = event, navController)
            }
        }
    }
}




fun getSampleEvents(): List<Event> {
    return listOf(
        Event(
            contentId = 0,
            isTour = true,
            backgroundImageUrl = "https://cdn.albumoftheyear.org/artists/sq/nayt_1727482355.jpg",
            title = "Live@Roma",
            artist = getSampleArtist().get(0),
            description = "Atlantico - Roma",
            date = "2025/10/13"
        ),
        Event(
            contentId = 0,
            isTour = false,
            backgroundImageUrl = "https://cdn.albumoftheyear.org/artists/sq/nayt_1727482355.jpg",
            title = "Live@Bologna",
            artist = getSampleArtist().get(1),

            description = "Atlantico - Roma",
            date = "2025/10/13"
        ),
        Event(
            contentId = 0,
            isTour = false,
            backgroundImageUrl = "https://cdn.albumoftheyear.org/artists/sq/nayt_1727482355.jpg",
            title = "Live@Chicago",
            artist = getSampleArtist().get(2),
            description = "Atlantico - Roma",
            date = "2025/10/13"
        ),
        Event(
            contentId = 0,
            isTour = false,
            backgroundImageUrl = "https://cdn.albumoftheyear.org/artists/sq/nayt_1727482355.jpg",
            title = "Live@Nonantola",
            artist = getSampleArtist().get(3),
            description = "Atlantico - Roma",
            date = "2025/10/13"
        )

    )
}

fun getSampleArtist(): List<Artist>{
    return listOf(
        Artist(
            artistId = 0,
            artistName = "Nayt",
            artistImageUrl = "https://imgs.search.brave.com/s89lVzcDdhWkaaSUd1BssVMKSbHez2vKwmPHYbOX0gI/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9uYXl0/bWVyY2guY29tL3dw/LWNvbnRlbnQvdXBs/b2Fkcy8yMDIzLzA4/L05heXQtTWVyY2gt/MS5wbmc",
            likesCount = 4800,
            averageRating = 4.8f
        ),
        Artist(
            artistId = 0,
            artistName = "RosoloRoso",
            artistImageUrl = "https://imgs.search.brave.com/s89lVzcDdhWkaaSUd1BssVMKSbHez2vKwmPHYbOX0gI/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9uYXl0/bWVyY2guY29tL3dw/LWNvbnRlbnQvdXBs/b2Fkcy8yMDIzLzA4/L05heXQtTWVyY2gt/MS5wbmc",
            likesCount = 4800,
            averageRating = 4.8f
        ),
        Artist(
            artistId = 0,
            artistName = "Sfera",
            artistImageUrl = "https://imgs.search.brave.com/s89lVzcDdhWkaaSUd1BssVMKSbHez2vKwmPHYbOX0gI/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9uYXl0/bWVyY2guY29tL3dw/LWNvbnRlbnQvdXBs/b2Fkcy8yMDIzLzA4/L05heXQtTWVyY2gt/MS5wbmc",
            likesCount = 4800,
            averageRating = 4.8f
        ),
        Artist(
            artistId = 0,
            artistName = "Izi",
            artistImageUrl = "https://imgs.search.brave.com/s89lVzcDdhWkaaSUd1BssVMKSbHez2vKwmPHYbOX0gI/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9uYXl0/bWVyY2guY29tL3dw/LWNvbnRlbnQvdXBs/b2Fkcy8yMDIzLzA4/L05heXQtTWVyY2gt/MS5wbmc",
            likesCount = 4800,
            averageRating = 4.8f
        )
    )
}

