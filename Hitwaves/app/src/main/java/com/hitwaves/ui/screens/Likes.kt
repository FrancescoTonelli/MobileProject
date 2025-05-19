package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.model.Artist
import com.hitwaves.ui.component.ArtistCard
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.Title
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.viewModel.LikesViewModel
import com.hitwaves.ui.theme.*

private fun init() : LikesViewModel {
    return LikesViewModel()
}

@Composable
fun Likes(navController: NavHostController) {
    val favouritesArtist = remember { mutableStateOf<List<Artist>>(emptyList()) }

    val likesViewModel = remember { init() }
    val result by likesViewModel.likedArtistsState
    val isLoading by likesViewModel.isLikesLoading
    val toggleState by likesViewModel.toggleState
    val errorMsg = remember { mutableStateOf("") }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        likesViewModel.getLikedArtists()
    }

    LaunchedEffect(result) {
        if (result.success && result.data != null) {
            favouritesArtist.value = result.data!!.map { artist ->
                Artist(
                    artistId = artist.id,
                    artistName = artist.name,
                    artistImageUrl = artist.image,
                    likesCount = artist.likesCount,
                    averageRating = artist.averageRating,
                    isLiked = true
                )
            }
        } else if (!result.success && result.errorMessage != null) {
            snackbarHostState.showSnackbar(result.errorMessage!!)
        }
    }

    LaunchedEffect(toggleState) {
        if (!toggleState.success && toggleState.errorMessage != null) {
            errorMsg.value = toggleState.errorMessage!!
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Title("Favourite artists")
            }

            if (favouritesArtist.value.isNotEmpty()) {
                items(favouritesArtist.value) { artist ->
                    ArtistCard(
                        artist = artist,
                        onLikeClick = {
                            artistId -> likesViewModel.toggleLike(artistId)
                        }
                    )
                }
            } else {
                item {
                    Text(
                        text = "No favourite artists found",
                        style = Typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Secondary
                        )
                    )
                }
            }
        }
    }


    CustomSnackbar(snackbarHostState)

    if (isLoading) {
        LoadingIndicator()
    }
}