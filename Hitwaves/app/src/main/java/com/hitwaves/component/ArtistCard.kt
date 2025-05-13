package com.hitwaves.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.model.Artist
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.rememberScreenDimensions
import com.hitwaves.R

@Composable
fun ArtistCard(artist: Artist){
    var isLiked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(artist.artistImageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ){
                Text(
                    text = artist.artistName,
                    color = Secondary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    //modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    text = "${artist.likesCount} likes",
                    color = Secondary,
                    fontSize = 14.sp,
                    //modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    Text(
                        text = "${artist.averageRating}",
                        color = Secondary,
                        fontSize = 16.sp
                    )

                    Icon(
                        ImageVector.vectorResource(R.drawable.favourites),
                        tint = Secondary,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { isLiked = !isLiked }
            ){
                Icon(
                    imageVector = if (isLiked) ImageVector.vectorResource(R.drawable.like_fill) else ImageVector.vectorResource(R.drawable.like_line),
                    contentDescription = null,
                    tint = Secondary
                )
            }
        }


        HorizontalDivider(
            modifier = Modifier
                .padding(top = 8.dp)
                .width(rememberScreenDimensions().screenWidth * 0.9f),
            thickness = 1.dp,
            color = Secondary
        )
    }
}