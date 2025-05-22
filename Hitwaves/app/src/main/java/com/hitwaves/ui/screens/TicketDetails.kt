package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hitwaves.R
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.TicketViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun init() : TicketViewModel {
    return TicketViewModel()
}

@Composable
fun TicketDetails(eventForCards: EventForCards, navController: NavController) {
    val ticketViewModel = remember { init() }
    val details by ticketViewModel.detailsState
    val loading by ticketViewModel.isLoadingDetails

    var isClickable by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (eventForCards.isTicket) {
            snackBarHostState.showSnackbar(
                message = "Ticket already purchased"
            )
        }
        ticketViewModel.getTicketDetails(eventForCards.contentId)
    }

    LaunchedEffect(details) {
        if (!details.success && details.errorMessage.isNullOrEmpty().not()) {
            snackBarHostState.showSnackbar(
                details.errorMessage.toString()
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
    }

    if (loading) {
        LoadingIndicator()
    }

    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GoBack(navController = navController)

        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(35.dp)
                .clip(CircleShape)
                .background(Primary)
                .clickable(enabled = isClickable) {
                    isClickable = false

                    // TODO: Aggiungere logica per salvataggio su calendario

                    scope.launch {
                        delay(500)
                        isClickable = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.back),
                contentDescription = null,
                tint = BgDark
            )
        }

    }
    CustomSnackBar(snackBarHostState = snackBarHostState)
}