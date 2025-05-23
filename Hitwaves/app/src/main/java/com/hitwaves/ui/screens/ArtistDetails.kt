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
import com.hitwaves.api.artistImageUrl
import com.hitwaves.api.getHttpArtistImageUrl
import com.hitwaves.api.getHttpUserImageUrl
import com.hitwaves.ui.component.ArtistCard
import com.hitwaves.ui.component.EventCard
import com.hitwaves.ui.component.Title
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.RatingViewOnly
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
                    description = concert.placeName
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
                artist = if(artistDetails.success && artistDetails.data != null) {
                    Artist(
                        artistId = artist.artistId,
                        artistName = artistDetails.data!!.artist.name,
                        artistImageUrl = artistDetails.data!!.artist.image,
                        likesCount = artistDetails.data!!.artist.likesCount,
                        averageRating = artistDetails.data!!.artist.averageRating.toFloat(),
                        isLiked = artistDetails.data!!.artist.isLiked
                    )
                }else{
                    artist
                },
                onLikeClick = { artistId ->
                    likesViewModel.toggleLike(artistId)
                },
                onClick = {}
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
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
                            .padding(vertical = 10.dp)
                            .width(rememberScreenDimensions().screenWidth * 0.9f),
                        thickness = 1.dp,
                        color = Secondary
                    )
                }

                item {
                    Title("Reviews")
                }

                if(reviews.isEmpty()){
                    item {
                        Text(
                            text = "No reviews yet",
                            style = Typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = Secondary
                            )
                        )
                    }
                }else {
                    items(reviews) { review ->
                        ReviewCard(review)
                    }
                }
            }
        }


        GoBack(navController)

        CustomSnackBar(snackbarHostState)

        if (isLoading) {
            LoadingIndicator()
        }
    }

    GoBack(navController)
}

@Composable
fun ReviewCard(review : ReviewArtistDetailsResponse){
    Box(
        modifier = Modifier
            .fillMaxSize(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .background(FgDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            //horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize(0.9f)
                .padding(vertical = 24.dp, horizontal = 15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(50.dp)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(getHttpUserImageUrl(review.userImage)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                Column(
                    //horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = review.username,
                        style = Typography.titleLarge.copy(
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Text(
                        text = review.concertTitle,
                        style = Typography.titleLarge.copy(
                            fontSize = 14.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Text(
                        text = review.concertDate,
                        style = Typography.titleLarge.copy(
                            fontSize = 14.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }


            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = Secondary.copy(alpha = 0.8f)
            )


            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                RatingViewOnly(
                    rating = review.rate,
                    starSize = 18.dp,
                    starSpacing = 3.dp
                )

                if (!review.description.isNullOrEmpty()) {
                    Text(
                        text = review.description,
                        style = Typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}