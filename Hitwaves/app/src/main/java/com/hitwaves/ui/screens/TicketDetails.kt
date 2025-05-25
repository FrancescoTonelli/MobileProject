package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hitwaves.R
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.TicketDisplayFuture
import com.hitwaves.ui.component.TicketDisplayPast
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography
import com.hitwaves.ui.viewModel.TicketViewModel
import com.hitwaves.utils.createCalendarIntent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun init() : TicketViewModel {
    return TicketViewModel()
}

@Composable
fun TicketDetails(eventForCards: EventForCards, navController: NavController, innerPadding: PaddingValues) {
    val ticketViewModel = remember { init() }
    val details by ticketViewModel.detailsState
    val loading by ticketViewModel.isLoadingDetails
    val qr by ticketViewModel.qrState
    val loadingQr by ticketViewModel.isLoadingQr

    var isClickable by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        if (details.success && details.data != null) {
            if (ticketViewModel.isFutureOrToday(details.data!!.concertDate)) {
                TicketDisplayFuture(details, qr, loadingQr)
            }
            else {
                TicketDisplayPast(details, innerPadding)
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

                            val intent = createCalendarIntent(
                                eventName = details.data!!.concertTitle,
                                date = details.data!!.concertDate,
                                time = details.data!!.concertTime,
                                location = details.data!!.placeAddress
                            )

                            if (intent != null) {
                                context.startActivity(intent)
                            }


                            scope.launch {
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
