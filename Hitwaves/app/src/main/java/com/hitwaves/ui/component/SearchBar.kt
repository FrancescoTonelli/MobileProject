package com.hitwaves.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hitwaves.R
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.FilterViewModel
import androidx.compose.material3.SnackbarHostState
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController


private fun init(): FilterViewModel{
    return FilterViewModel()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchWave(
    query: String,
    onQueryChange: (String) -> Unit,
    navController: NavController
){
    val filterViewModel = remember { init() }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val isLoading by filterViewModel.isLoadingFilter
    val allArtist by filterViewModel.allArtistState
    val allConcert by filterViewModel.allConcertState
    val allTour by filterViewModel.allTourState

    var allArtistSearch : List<Artist> by remember { mutableStateOf(emptyList()) }
    var allConcertSearch : List<EventForCards> by remember { mutableStateOf(emptyList()) }
    var allTourSearch : List<EventForCards> by remember { mutableStateOf(emptyList()) }

    val snackBarHostState = remember { SnackbarHostState() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        filterViewModel.getAllArtist()
        filterViewModel.getAllEvent()
        filterViewModel.getAllTour()
    }

    LaunchedEffect(allArtist) {
        if (allArtist.success && allArtist.data != null) {
            allArtistSearch = allArtist.data!!.map { artist ->
                Artist(
                    artistId = artist.id,
                    artistName = artist.name,
                    artistImageUrl = artist.image,
                    likesCount = artist.likesCount,
                    averageRating = artist.averageRating,
                    isLiked = false
                )
            }
        } else if (!allArtist.success && allArtist.errorMessage != null) {
            snackBarHostState.showSnackbar(allArtist.errorMessage!!)
        }
    }

    LaunchedEffect(allConcert) {
        if (allConcert.success && allConcert.data != null) {
            allConcertSearch = allConcert.data!!.map { event ->
                EventForCards(
                    contentId = event.id,
                    isTour = false,
                    backgroundImage = event.image.orEmpty(),
                    title = event.title,
                    artistName = event.artistName?: "Unknown",
                    artistImage = event.artistImage.orEmpty(),
                    description = event.placeName,
                    date = event.date,
                    placeName = event.placeName
                )
            }
        } else if (!allConcert.success && allConcert.errorMessage != null) {
            snackBarHostState.showSnackbar(allConcert.errorMessage!!)
        }
    }

    LaunchedEffect(allTour) {
        if (allTour.success && allTour.data != null) {
            allTourSearch = allTour.data!!.map { event ->
                EventForCards(
                    contentId = event.tourId,
                    isTour = true,
                    backgroundImage = event.tourImage.orEmpty(),
                    title = event.tourTitle,
                    artistName = event.artistName,
                    artistImage = event.artistImage.orEmpty(),
                    description = "Tour - ${event.concertCount} shows",
                    date = null,
                    placeName = null
                )
            }
        } else if (!allTour.success && allTour.errorMessage != null) {
            snackBarHostState.showSnackbar(allTour.errorMessage!!)
        }
    }

    BackHandler(enabled = expanded) {
        expanded = false
        onQueryChange("")
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    val filteredArtists = remember(query, allArtistSearch) {
        allArtistSearch.filter { it.artistName.contains(query, ignoreCase = true) }
    }


    val filteredConcerts = remember(query, allConcertSearch) {
        allConcertSearch.filter { it.title.contains(query, ignoreCase = true) ||
                (it.placeName?: "").contains(query, ignoreCase = true) ||
                it.artistName.contains(query, ignoreCase = true)}
    }

    val filteredTours = remember(query, allTourSearch) {
        allTourSearch.filter { it.title.contains(query, ignoreCase = true) ||
                it.artistName.contains(query, ignoreCase = true)}
    }

    val filteredEvents = filteredConcerts + filteredTours

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField(
            query = query,
            onQueryChange = {
                onQueryChange(it)
                expanded = true
            },
            onSearch = {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.search),
                    tint = Secondary,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                )
            },
            placeholder = {
                Text(
                    text = "Search your wave...",
                    style = Typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = Secondary.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Normal
                    )
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .width(rememberScreenDimensions().screenWidth*0.9f)
                .padding(top = 16.dp)
                .border(1.dp, Secondary, CircleShape),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BgDark,
                unfocusedContainerColor = BgDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Secondary,
                focusedTextColor = Secondary,
                unfocusedTextColor = Secondary
            )
        )
        if (expanded) {

            Spacer(modifier = Modifier.height(16.dp))

            ShowArtistList(filteredArtists, navController)

            HorizontalDivider(
                modifier = Modifier
                    .width(rememberScreenDimensions().screenWidth * 0.85f)
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                thickness = 1.dp,
                color = Secondary.copy(alpha = 0.5f),
                )
            ShowEventList(filteredEvents, navController)
        }
    }

    if (isLoading) {
        LoadingIndicator()
    }
}

@Composable
fun ShowArtistList(artistList: List<Artist>, navController: NavController) {
    var selectedArtistId by remember { mutableStateOf<Int?>(null) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        if(artistList.isNotEmpty()){
            items(artistList){ artist ->
                MinimalArtist(
                    artist = artist,
                    isSelected = artist.artistId == selectedArtistId,
                    onClick = {
                        selectedArtistId = artist.artistId

                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("artist", artist)

                        navController.navigate("artistDetails")
                    }
                )
            }
        }
    }
}

@Composable
fun ShowEventList(eventForCardsList: List<EventForCards>, navController: NavController){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (eventForCardsList.isNotEmpty()) {
            items(eventForCardsList) { event ->
                EventCard(event = event, navController)
            }
        }
    }
}