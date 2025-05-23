package com.hitwaves.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TicketDetailsResponse
import com.hitwaves.api.getHttpConcertImageUrl
import com.hitwaves.api.getHttpTourImageUrl
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography

@Composable
fun TicketDisplayPast(details: ApiResult<TicketDetailsResponse>) {

    val ratingNew = remember { mutableIntStateOf(0) }
    val descriptionNew = remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = rememberAsyncImagePainter(if (details.data?.tourTitle.isNullOrEmpty()) {
                        getHttpConcertImageUrl(details.data?.concertImage)
                    } else {
                        getHttpTourImageUrl(details.data?.concertImage)
                    }),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

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
                        (if (details.success) {
                            if (details.data?.tourTitle.isNullOrEmpty()) {
                                details.data?.concertTitle
                            } else {
                                "${details.data?.tourTitle} - ${details.data?.concertTitle}"
                            }
                        } else {
                            details.errorMessage.toString()
                        })?.let {
                            Text(
                                text = it,
                                style = Typography.titleLarge.copy(
                                    fontSize = 18.sp,
                                    color = Secondary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Box (
                        modifier = Modifier
                            .background(FgDark)
                    ){
                        Text(
                            text = "Concert",
                            style = Typography.titleLarge.copy(
                                fontSize = 12.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Title(
                    title = "Details"
                )

                DetailRow(
                    label = "Place",
                    value = details.data!!.placeName,
                    displayDivider = false
                )
                GmapsDetailRow(
                    label = "Address",
                    value = details.data.placeAddress,
                    displayDivider = false
                )
                DetailRow(
                    label = "Date",
                    value = details.data.concertDate,
                    displayDivider = false
                )
                DetailRow(
                    label = "Time",
                    value = details.data.concertTime,
                    displayDivider = false
                )

                Spacer(modifier = Modifier.height(16.dp))
                Title(
                    title = "Rate your experience"
                )

                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Rating(
                        rating = ratingNew.intValue,
                        starSize = 25.dp,
                        starSpacing = 7.dp,
                        onChange = { value ->
                            ratingNew.intValue = value
                        }
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Description",
                        style = Typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = descriptionNew.value,
                    onValueChange = { value -> descriptionNew.value = value },
                    placeholder = {
                        Text(
                            text = "Write your review here (optional)",
                            style = Typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                color = Secondary.copy(alpha = 0.5f)
                            )
                        )
                    },
                    maxLines = 5,
                    textStyle = Typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = Secondary
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FgDark,
                        unfocusedContainerColor = FgDark,
                        cursorColor = Primary,
                        focusedIndicatorColor = Primary,
                        unfocusedIndicatorColor = Secondary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )

                Spacer(modifier = Modifier.height(60.dp))
                ButtonWithIcons(
                    startIcon = ImageVector.vectorResource(R.drawable.airplane),
                    textBtn = "Post",
                    endIcon = ImageVector.vectorResource(R.drawable.arrow),
                    onClickAction = {
                        // TODO("Post review")
                    }
                )
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}