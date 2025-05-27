package com.hitwaves.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.hitwaves.ui.viewModel.ReviewsViewModel
import kotlinx.coroutines.launch

private fun init() : ReviewsViewModel {
    return ReviewsViewModel()
}

@Composable
fun TicketDisplayPast(details: ApiResult<TicketDetailsResponse>, innerPadding: PaddingValues) {

    val ratingNew = remember { mutableIntStateOf(0) }
    val descriptionNew = remember { mutableStateOf("") }

    val reviewsViewModel = remember { init() }
    val existingState by reviewsViewModel.checkReviewState
    val postState by reviewsViewModel.postReviewState
    val isLoading by reviewsViewModel.isLoading

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        reviewsViewModel.checkExistence(details.data!!.concertId)
    }

    LaunchedEffect(existingState) {
        if (!existingState.success && !isLoading) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(existingState.errorMessage ?: "An error occurred while checking review existence.")
            }
        }
    }

    LaunchedEffect(postState) {
        if (postState.success) {
            reviewsViewModel.checkExistence(details.data!!.concertId)
            coroutineScope.launch {
                snackBarHostState.showSnackbar(postState.data?.message?:"Review posted successfully!")
            }
        } else if (postState.errorMessage != null) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(postState.errorMessage!!)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .consumeWindowInsets(innerPadding)
            .imePadding(),
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

                if (existingState.success && existingState.data != null){
                    if (!existingState.data!!.hasReviewed) {
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

                        Spacer(modifier = Modifier.height(40.dp))
                        ButtonWithIcons(
                            startIcon = ImageVector.vectorResource(R.drawable.airplane),
                            textBtn = "Post",
                            endIcon = ImageVector.vectorResource(R.drawable.arrow),
                            onClickAction = {
                                reviewsViewModel.postReview(
                                    ticketId = details.data.ticketId,
                                    rating = ratingNew.intValue,
                                    comment = descriptionNew.value
                                )
                            }
                        )
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "You have already reviewed this concert. See Account > Your Reviews to edit your review.",
                                style = Typography.bodyLarge.copy(
                                    fontSize = 18.sp,
                                    color = Secondary,
                                    fontWeight = FontWeight.Normal
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                else if(!existingState.success && existingState.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error checking review existence: ${existingState.errorMessage}",
                            style = Typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Loading...",
                            style = Typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else{
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "An error occurred while checking review existence.",
                            style = Typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Normal
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if(isLoading) {
        LoadingIndicator()
    }

    CustomSnackBar(snackBarHostState)
}