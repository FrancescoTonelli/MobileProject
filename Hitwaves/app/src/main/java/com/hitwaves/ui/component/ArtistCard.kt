package com.hitwaves.ui.component

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.model.Artist
import com.hitwaves.ui.theme.*
import com.hitwaves.R
import com.hitwaves.api.getHttpArtistImageUrl

@Composable
fun ArtistCard(
    artist: Artist,
    onLikeClick: (Int) -> Unit
) {
    var isLiked by remember { mutableStateOf(artist.isLiked) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clickable {
                    // Handle click event
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(getHttpArtistImageUrl(artist.artistImageUrl)),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    Text(
                        text = artist.artistName,
                        style = Typography.titleLarge.copy(
                            fontSize = 24.sp,
                            color = Secondary
                        )
                    )

                    Text(
                        text = "${artist.likesCount} likes",
                        style = Typography.bodyLarge.copy(
                            fontSize = 14.sp,
                            color = Secondary
                        )
                    )

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Text(
                            text = "${artist.averageRating}",
                            style = Typography.bodyLarge.copy(
                                fontSize = 14.sp,
                                color = Secondary
                            )
                        )

                        Icon(
                            ImageVector.vectorResource(R.drawable.star_fill),
                            tint = Secondary,
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = {
                        isLiked = !isLiked
                        onLikeClick(artist.artistId)
                    }
                ){
                    Icon(
                        imageVector = if (isLiked) ImageVector.vectorResource(R.drawable.like_fill) else ImageVector.vectorResource(R.drawable.like_line),
                        contentDescription = null,
                        tint = Secondary
                    )
                }
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