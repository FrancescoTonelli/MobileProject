package com.hitwaves.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.model.Artist
import com.hitwaves.ui.component.EventCard
import com.hitwaves.ui.component.ShowArtistList
import com.hitwaves.ui.component.Title
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.theme.rememberScreenDimensions
import com.hitwaves.ui.viewModel.TourViewModel

private fun init(): TourViewModel {
    return TourViewModel()
}

@Composable
fun TourDetails(eventForCards: EventForCards, navController: NavController){

    val tourViewModel = remember { init() }
    val tour by tourViewModel.tourState
    val isLoading by tourViewModel.isLoadingTour

    var tourArtistShow : List<Artist> by remember { mutableStateOf(emptyList()) }
    var tourConcertShow : List<EventForCards> by remember { mutableStateOf(emptyList()) }

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        tourViewModel.getTourDetails(eventForCards.contentId)
        //tourViewModel.getTourConcert(eventForCards.contentId)
    }

    LaunchedEffect(tour) {
        if (tour.success && tour.data != null) {
            tourArtistShow = tour.data!!.artists.map { artist ->
                Artist(
                    artistId = artist.artistId,
                    artistName = artist.artistName,
                    artistImageUrl = artist.artistImage,
                    likesCount = null,
                    averageRating = null
                )
            }

            val firstArtist = tour.data!!.artists.first()

            tourConcertShow = tour.data!!.concerts.map { event ->
                EventForCards(
                    contentId = event.concertId,
                    isTour = false,
                    backgroundImage = event.concertImage.orEmpty(),
                    title = event.concertTitle,
                    artistName = firstArtist.artistName,
                    artistImage = firstArtist.artistImage.orEmpty(),
                    description = event.placeName,
                    date = event.concertDate,
                    placeName = event.placeName
                )
            }
        } else if (!tour.success && tour.errorMessage != null) {
            snackBarHostState.showSnackbar(tour.errorMessage!!)
        }
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .size(rememberScreenDimensions().screenWidth, 150.dp)
        ){
            Image(
                painter = rememberAsyncImagePainter(eventForCards.backgroundImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom

            ) {
                Box (
                    modifier = Modifier
                        .background(Primary)
                ){
                    Text(
                        text = eventForCards.title,
                        color = Secondary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Box (
                    modifier = Modifier
                        .background(FgDark)
                ){
                    eventForCards.description?.let {
                        Text(
                            text = it,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
            GoBack(navController)
        }

        LazyColumn (
            modifier = Modifier
                .width(rememberScreenDimensions().screenWidth*0.9f)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item {
                Title("Artists")
            }
            item {
                ShowArtistList(tourArtistShow, navController)
            }
            item {
                Title("Shows")
            }

            if(tourConcertShow.isNotEmpty()){
                items(tourConcertShow) { event ->
                    EventCard(event, navController)
                }
            }
        }
    }

    CustomSnackBar(snackBarHostState)


    if (isLoading) {
        LoadingIndicator()
    }
}
