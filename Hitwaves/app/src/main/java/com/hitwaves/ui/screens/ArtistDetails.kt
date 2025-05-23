package com.hitwaves.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.R
import com.hitwaves.api.ConcertArtistDetailsResponse
import com.hitwaves.api.ReviewArtistDetailsResponse
import com.hitwaves.api.TourArtistDetailsResponse
import com.hitwaves.api.getHttpArtistImageUrl
import com.hitwaves.ui.component.ArtistCard
import com.hitwaves.ui.component.EventCard
import com.hitwaves.ui.component.Title
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.viewModel.ArtistViewModel
import com.hitwaves.ui.viewModel.LikesViewModel

private fun initLikes() : LikesViewModel{
    return LikesViewModel()
}

private fun init(): ArtistViewModel {
    return ArtistViewModel()
}

@Composable
fun ArtistDetails(artist: Artist, navController: NavController){

    val artistViewModel = remember { init() }
    val likesViewModel = remember { initLikes() }
    val artistDetails by artistViewModel.artistState
    val isLoading by artistViewModel.isLoadingArtist
    val toggleState by likesViewModel.toggleState


    val snackbarHostState = remember { SnackbarHostState() }

    var nextConcert : List<EventForCards> by remember { mutableStateOf(emptyList()) }
    var nextTour : List<EventForCards> by remember { mutableStateOf(emptyList()) }
    var reviews: List<ReviewArtistDetailsResponse> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        artistViewModel.getArtistInfo(artist.artistId)
    }

    LaunchedEffect(artistDetails) {
        if(artistDetails.success && artistDetails.data != null){
            nextConcert = artistDetails.data!!.concerts.map { concert ->
                EventForCards(
                    contentId = concert.id,
                    title = concert.title,
                    backgroundImage = concert.image.orEmpty(),
                    date = concert.date,
                    placeName =  concert.placeName,
                    isTour = false,
                    isTicket = false,
                    artistName = artistDetails.data!!.artist.name,
                    artistImage = artistDetails.data!!.artist.image.orEmpty(),
                    description = null
                )
            }

            nextTour = artistDetails.data!!.tours.map { tour ->
                EventForCards(
                    contentId = tour.id,
                    isTour = true,
                    backgroundImage = tour.image.orEmpty(),
                    title = tour.title,
                    artistName = artistDetails.data!!.artist.name,
                    artistImage = artistDetails.data!!.artist.image.orEmpty(),
                    description = "Tour - ${tour.concertsCount} shows",
                    date = null,
                    placeName = null
                )
            }

            reviews = artistDetails.data!!.reviews
        }
    }

    val nextEvent = nextConcert + nextTour

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 25.dp)
        ) {

            ArtistCard(
                artist = artist,
                onLikeClick = { artistId ->
                    likesViewModel.toggleLike(artistId)
                }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Title("Next Event")
                }

                items(nextEvent) { event ->
                    EventCard(event = event, navController)
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .width(rememberScreenDimensions().screenWidth * 0.9f),
                        thickness = 1.dp,
                        color = Secondary
                    )
                }

                item {
                    Title("Reviews")
                }

                items(reviews) { review ->
                    ReviewCard(review)
                }
            }
        }


        GoBack(navController)

        CustomSnackbar(snackbarHostState)

        if (isLoading) {
            LoadingIndicator()
        }
    }

    GoBack(navController)
}

@Composable
fun ReviewCard(review : ReviewArtistDetailsResponse){
    Column(
        //horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .width(rememberScreenDimensions().screenWidth * 0.9f)
            .clip(RoundedCornerShape(15))
            .background(FgDark)
            .padding(8.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(50.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(getHttpArtistImageUrl(review.userImage)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            Column (
                //horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Text(
                    text = review.username,
                    style = Typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        color = Secondary,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Text(
                    text = "${ review.username } - ${ review.concertDate }",
                    style = Typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = Secondary,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }


        HorizontalDivider(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(290.dp)
                .align(Alignment.CenterHorizontally),
            thickness = 1.dp,
            color = Secondary.copy(alpha = 0.8f)
        )

        //TODO:missing rating star

        review.description?.let {
            Text(
                text = it,
                style = Typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = Secondary,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}