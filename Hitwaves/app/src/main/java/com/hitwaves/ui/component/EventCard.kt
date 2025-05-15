package com.hitwaves.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.model.Event
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.theme.rememberScreenDimensions

@Composable
fun EventCard(event: Event, navController: NavController){
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Card(
            modifier = Modifier
                .width(rememberScreenDimensions().screenWidth * 0.9f)
                .height(200.dp)
                .clickable {
                    navController.currentBackStackEntry?.savedStateHandle?.set("event", event)
                    navController.navigate("eventDetails"){
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }

                },
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(3.dp, FgDark),
        ){
            Box{
                Image(
                    painter = rememberAsyncImagePainter(event.backgroundImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (event.date != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .background(FgDark)
                    ) {
                        Text(
                            text = event.date,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                ShowDescription(event)
            }
        }
    }
}

@Composable
fun ShowDescription(event: Event){
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
                text = event.title,
                color = Secondary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Row (
            modifier = Modifier
                .background(FgDark),

            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(16.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(event.artist.artistImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()

                )
            }
            Text(
                text = event.artist.artistName,
                fontSize = 12.sp,
                color = Secondary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

        }

        Box (
            modifier = Modifier
                .background(FgDark)
        ){
            Text(
                text = event.description,
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

    }
}