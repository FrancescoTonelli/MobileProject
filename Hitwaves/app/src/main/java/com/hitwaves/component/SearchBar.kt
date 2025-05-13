package com.hitwaves.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.R
import com.hitwaves.component.EventCard
import com.hitwaves.model.Artist
import com.hitwaves.model.Event
import com.hitwaves.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchResultsArtists: List<Artist>,
    searchResultsEvents: List<Event>,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    shape: Shape = RoundedCornerShape(30.dp),
    navController: NavController
){

    var expanded by rememberSaveable { mutableStateOf(false) }

    val filteredArtists = remember(query, searchResultsArtists) {
        searchResultsArtists.filter { it.artistName.contains(query, ignoreCase = true) }
    }

    val filteredEvents = remember(query, searchResultsEvents) {
        searchResultsEvents.filter { it.title.contains(query, ignoreCase = true) }
    }


    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        SearchBar(
//            query = query,
//            onQueryChange = {
//                onQueryChange(it)
//                expanded = it.isNotBlank()
//            },
//            onSearch = { expanded = false },
//            active = expanded,
//            onActiveChange = { expanded = it },
//            placeholder = {
//                Text(
//                    text = "Search your wave...",
//                    color = Secondary.copy(alpha = 0.5f)
//                ) },
//            leadingIcon = {
//                Icon(
//                    ImageVector.vectorResource(R.drawable.search),
//                    tint = Secondary,
//                    contentDescription = null
//                )
//            },
//            colors = colors,
//            shape = shape,
//            modifier = Modifier
//                .width(rememberScreenDimensions().screenWidth * 0.9f)
//        )
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = {
                        onQueryChange(it)
                        expanded = it.isNotBlank()
                    },
                    onSearch = { expanded = false },
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.search),
                            tint = Secondary,
                            contentDescription = null
                        )
                    },
                    placeholder = {
                        Text(
                            text = "Search your wave...",
                            color = Secondary.copy(alpha = 0.5f)
                        )
                    },
                    colors = colors,
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .width(rememberScreenDimensions().screenWidth * 0.9f)
        ) {
            if(expanded){
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
    }
}


@Composable
fun MinimalArtist(
    artist: Artist,
    isSelected: Boolean,
    onClick: () -> Unit
){
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .background(if (isSelected) Primary.copy(alpha = 0.2f) else Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(50.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = rememberAsyncImagePainter(artist.artistImageUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()

            )
        }
        Text(
            text = artist.artistName,
            fontSize = 16.sp,
            color = Secondary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
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

                        navController.navigate("artistDetails"){
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ShowEventList(eventList: List<Event>, navController: NavController){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (eventList.isNotEmpty()) {
            items(eventList) { event ->
                EventCard(event = event, navController)
            }
        }
    }
}