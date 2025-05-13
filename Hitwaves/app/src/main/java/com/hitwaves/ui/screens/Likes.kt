package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.component.ArtistCard
import com.hitwaves.component.Title
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.theme.rememberScreenDimensions

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