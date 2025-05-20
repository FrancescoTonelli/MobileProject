package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hitwaves.R
import com.hitwaves.ui.component.ArtistCard
import com.hitwaves.ui.component.EventCard
import com.hitwaves.ui.component.Title
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.LikesViewModel
import com.hitwaves.ui.component.GoBack

fun initLikes() : LikesViewModel {
    return LikesViewModel()
}

@Composable
fun ArtistDetails(artist: Artist, navController: NavController){
    val eventList = emptyList<EventForCards>()

    GoBack(navController)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .padding(top = 30.dp)
        ){
            ArtistCard(
                artist = artist,
                onLikeClick = {
                    artistId -> {}
                }
            )
        }

        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                Title("Next Event")
            }

            items(eventList) { event ->
                EventCard(event = event, navController)
            }
        }
    }
}
