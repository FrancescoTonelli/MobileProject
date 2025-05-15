package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hitwaves.ui.component.ArtistCard
import com.hitwaves.ui.component.Title

@Composable
fun Likes(navController: NavHostController) {
    val favouritesArtist = getSampleArtist()

    Column (
        modifier = Modifier
            .fillMaxWidth(),
            //.width(rememberScreenDimensions().screenWidth * 0.9f),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        LazyColumn (
            modifier = Modifier
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Title("Favourite artist")
            }

            if (favouritesArtist.isNotEmpty()){
                items(favouritesArtist) { artist ->
                    ArtistCard(artist)
                }
            }
        }
    }
}