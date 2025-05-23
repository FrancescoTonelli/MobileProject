package com.hitwaves.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.hitwaves.R
import com.hitwaves.api.UserReviewResponses
import com.hitwaves.api.getHttpArtistImageUrl
import com.hitwaves.api.getHttpUserImageUrl
import com.hitwaves.ui.theme.*

@Composable
fun AccountReviewCard(
    userReviewResponses: UserReviewResponses,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .background(FgDark),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp, horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(50.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(
                                getHttpArtistImageUrl(userReviewResponses.artistImage)
                            ),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column (
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "on ${userReviewResponses.artistName}",
                            style = Typography.titleLarge.copy(
                                fontSize = 18.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            ),
                        )

                        Text(
                            text = userReviewResponses.concertTitle,
                            style = Typography.titleLarge.copy(
                                fontSize = 14.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Normal
                            ),
                        )

                        Text(
                            text = userReviewResponses.concertDate,
                            style = Typography.titleLarge.copy(
                                fontSize = 14.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Normal
                            ),
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onDelete()
                    }
                ){
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.delete),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                thickness = 1.dp,
                color = Secondary
            )

            Column (
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {

                RatingViewOnly(
                    rating = userReviewResponses.rating,
                    starSize = 18.dp,
                    starSpacing = 3.dp
                )

                if (!userReviewResponses.comment.isNullOrEmpty()) {
                    Text(
                        text = userReviewResponses.comment,
                        style = Typography.titleLarge.copy(
                            fontSize = 14.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Normal
                        ),
                    )
                }
            }
        }
    }

}