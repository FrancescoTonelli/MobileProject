package com.hitwaves.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TicketDetailsResponse
import com.hitwaves.api.TicketQrResponse
import com.hitwaves.api.TokenManager
import com.hitwaves.api.getHttpConcertImageUrl
import com.hitwaves.api.getHttpTourImageUrl
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.DetailRow
import com.hitwaves.ui.component.GmapsDetailRow
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.QrCodeView
import com.hitwaves.ui.component.Rating
import com.hitwaves.ui.component.ShowArtistList
import com.hitwaves.ui.component.TicketDisplayFuture
import com.hitwaves.ui.component.TicketDisplayPast
import com.hitwaves.ui.component.Title
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.TicketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

private fun init() : TicketViewModel {
    return TicketViewModel()
}

@Composable
fun TicketDetails(eventForCards: EventForCards, navController: NavController) {
    val ticketViewModel = remember { init() }
    val details by ticketViewModel.detailsState
    val loading by ticketViewModel.isLoadingDetails
    val qr by ticketViewModel.qrState
    val loadingQr by ticketViewModel.isLoadingQr

    var isClickable by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        ticketViewModel.getTicketDetails(eventForCards.contentId)
    }

    LaunchedEffect(details) {
        if (!details.success && details.errorMessage.isNullOrEmpty().not()) {
            snackBarHostState.showSnackbar(details.errorMessage.toString())
        } else if (
            details.success &&
            details.data != null &&
            ticketViewModel.isFutureOrToday(details.data!!.concertDate)
        ) {
            ticketViewModel.getTicketQr(details.data!!.ticketId, details.data!!.concertId)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {

        if (details.success && details.data != null) {
            if (ticketViewModel.isFutureOrToday(details.data!!.concertDate)) {
                TicketDisplayFuture(details, qr, loadingQr)
            }
            else {
                TicketDisplayPast(details)
            }
        }
        else {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = details.errorMessage.toString(),
                    style = Typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        color = Secondary
                    )
                )
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GoBack(navController = navController)

            if (
                details.success &&
                details.data != null &&
                ticketViewModel.isFutureOrToday(details.data!!.concertDate)
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                        .background(Primary)
                        .clickable(enabled = isClickable) {
                            isClickable = false

                            // TODO: Add ticket to calendar

                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    "Ticket added to your calendar"
                                )
                                delay(500)
                                isClickable = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.calendar_add),
                        contentDescription = null,
                        tint = FgDark
                    )
                }
            }

        }

    }

    if (loading) {
        LoadingIndicator()
    }

    CustomSnackBar(snackBarHostState = snackBarHostState)
}
