package com.hitwaves.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.model.Artist
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary

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