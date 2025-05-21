package com.hitwaves.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.R
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.model.SectorConcert
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.RatingViewOnly
import com.hitwaves.ui.component.ShowArtistList
import com.hitwaves.ui.component.Title
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.rememberScreenDimensions
import com.hitwaves.ui.viewModel.ConcertViewModel

private fun init(): ConcertViewModel {
    return ConcertViewModel()
}

@Composable
fun ConcertDetails(eventForCards: EventForCards, navController: NavController) {

    val concertViewModel = remember { init() }
    val concert by concertViewModel.concertState
    val loading by concertViewModel.isLoadingConcert

    val snackbarHostState = remember { SnackbarHostState() }

    var concertArtist : List<Artist> by remember { mutableStateOf(emptyList()) }
    var concertInfo: EventForCards? by remember { mutableStateOf(null) }
    var concertSector: List<SectorConcert> by remember { mutableStateOf(emptyList()) }
    var concertAddress: String? by remember { mutableStateOf(null) }
    var concertTime: String? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        concertViewModel.getConcertInfo(eventForCards.contentId)
    }

    LaunchedEffect(concert) {
        if (concert.success && concert.data != null) {
            val info = concert.data!!.concertInfo
            val mainArtist = concert.data!!.artists.firstOrNull()

            concertInfo =
                EventForCards(
                    contentId = eventForCards.contentId,
                    isTour = false,
                    title = info.concertTitle,
                    backgroundImage = info.concertImage?: "",
                    artistName = mainArtist?.artistName.orEmpty(),
                    artistImage = mainArtist?.artistImage.orEmpty(),
                    description = info.placeName,
                    date = info.concertDate,
                    placeName = info.placeName
                )


            concertAddress = info.placeAddress
            concertTime = info.concertTime

            concertArtist = concert.data!!.artists.map { artist ->
                Artist(
                    artistId = artist.artistId,
                    artistName = artist.artistName,
                    artistImageUrl = artist.artistImage,
                    likesCount = null,
                    averageRating = null
                )
            }

            concertSector = concert.data!!.sectors.map { sector ->
                SectorConcert(
                    id = sector.id,
                    name = sector.name,
                    isStage = sector.isStage == 1
                )
            }

        } else if (!concert.success && concert.errorMessage != null) {
            snackbarHostState.showSnackbar(concert.errorMessage!!)
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
                .width(rememberScreenDimensions().screenWidth * 0.9f)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item {
                Title("Artists")
            }
            item {
                if(concertArtist.isEmpty()){
                    Text("lista nulla")
                }

                ShowArtistList(concertArtist, navController)
            }

            item {
                Title("Details")
            }
            item {
                Column {
                    concertInfo?.description?.let { InformationRow("Place", it) }
                    concertAddress?.let { InformationRow("Address", it) }
                    concertInfo?.date?.let { InformationRow("Date", it) }
                    concertTime?.let { InformationRow("Time", it) }
                }
            }

            item {
                Title("Choose your ticket")
            }
        }
    }


    CustomSnackbar(snackbarHostState)


    if (loading) {
        LoadingIndicator()
    }
}

@Composable
fun InformationRow(title: String, description: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Secondary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = description,
            color = Secondary,
            fontSize = 18.sp
        )
    }
}