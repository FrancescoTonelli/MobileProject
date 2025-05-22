package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.SecondaryTextTabs
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.TicketViewModel
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.EventCard

private fun init() : TicketViewModel {
    return TicketViewModel()
}

@Composable
fun Tickets(navController: NavHostController) {

    val ticketViewModel: TicketViewModel = remember { init() }
    val tickets by ticketViewModel.ticketsState
    val isLoading by ticketViewModel.isLoadingTickets
    val displayIndex by ticketViewModel.displayIndex

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        ticketViewModel.getTickets()
    }

    LaunchedEffect(tickets) {
        if(!tickets.success && !isLoading) {
            snackBarHostState.showSnackbar(
                message = tickets.errorMessage ?: "An error occurred",
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SecondaryTextTabs(ticketViewModel)

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn (
                modifier = Modifier
                    .fillMaxSize(0.9f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (displayIndex == 0) {
                    if (tickets.success && tickets.data != null) {
                        for( ticket in tickets.data!!) {
                            if (ticketViewModel.isFutureOrToday(ticket.concertDate)) {
                                item {
                                    val event = EventForCards(
                                        contentId = ticket.ticketId,
                                        isTour = !ticket.tourTitle.isNullOrEmpty(),
                                        placeName = ticket.placeName,
                                        isTicket = true,
                                        title = if (ticket.tourTitle.isNullOrEmpty()) ticket.concertTitle else "${ticket.tourTitle} - ${ticket.concertTitle}",
                                        backgroundImage = ticket.concertImage?:"",
                                        artistName = ticket.artistName?: "Unknown",
                                        artistImage = ticket.artistImage?:"",
                                        description = ticket.placeName,
                                        date = ticket.concertDate
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    EventCard(event = event, navController = navController)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                    else {
                        item {
                            Text(
                                text = "No Upcoming Events",
                                style = Typography.bodyLarge.copy(
                                    color = Secondary,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                } else if (displayIndex == 1) {
                    if (tickets.success && tickets.data != null) {
                        for( ticket in tickets.data!!) {
                            if (!ticketViewModel.isFutureOrToday(ticket.concertDate)) {
                                item {
                                    val event = EventForCards(
                                        contentId = ticket.ticketId,
                                        isTour = !ticket.tourTitle.isNullOrEmpty(),
                                        placeName = ticket.placeName,
                                        isTicket = true,
                                        title = if (ticket.tourTitle.isNullOrEmpty()) ticket.concertTitle else "${ticket.tourTitle} - ${ticket.concertTitle}",
                                        backgroundImage = ticket.concertImage?:"",
                                        artistName = ticket.artistName?: "Unknown",
                                        artistImage = ticket.artistImage?:"",
                                        description = ticket.placeName,
                                        date = ticket.concertDate
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    EventCard(event = event, navController = navController)
                                    Spacer(modifier = Modifier.height(8.dp))

                                }
                            }
                        }
                    }
                    else {
                        item {
                            Text(
                                text = "No Upcoming Events",
                                style = Typography.bodyLarge.copy(
                                    color = Secondary,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }


    CustomSnackBar(snackBarHostState)

    if (isLoading) {
        LoadingIndicator()
    }
}

