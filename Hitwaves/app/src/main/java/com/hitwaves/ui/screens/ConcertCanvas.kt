package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.InteractiveCanvasMap
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.viewModel.ConcertViewModel
import kotlinx.coroutines.launch
import com.hitwaves.ui.theme.*

private fun init(): ConcertViewModel {
    return ConcertViewModel()
}

@Composable
fun ConcertCanvas(navController: NavHostController, concertId: Int) {
    val concertViewModel = remember { init() }
    val canvasData by concertViewModel.canvasState
    val isLoadingCanvas by concertViewModel.isLoadingCanvas

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val ticketCart = remember { mutableStateListOf<Int>() }

    LaunchedEffect(Unit) {
        concertViewModel.getConcertCanvas(concertId)
    }

    LaunchedEffect(canvasData) {
        if(!isLoadingCanvas && canvasData.errorMessage != null) {
            snackBarHostState.showSnackbar(
                message = canvasData.errorMessage!!
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if(canvasData.success && canvasData.data != null) {
            InteractiveCanvasMap(
                tickets = canvasData.data?.tickets ?: emptyList(),
                sectors = canvasData.data?.sectors ?: emptyList(),
                onTicketClick = { ticket ->
                    if(ticket.ticketUserId == null) {
                        if (ticketCart.contains(ticket.ticketId)) {
                            ticketCart.remove(ticket.ticketId)
                        } else {
                            ticketCart.add(ticket.ticketId)
                        }
                    }
                },
                checkInCart = { ticketId ->
                    ticketCart.contains(ticketId)
                }
            )
        }
        else if (!isLoadingCanvas && canvasData.errorMessage != null) {
            Text(
                text = "Error loading canvas: ${canvasData.errorMessage}",
                style = Typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    color = Secondary,
                    fontWeight = FontWeight.Normal
                )
            )

        }
    }

    GoBack(navController)

    if(isLoadingCanvas) {
        LoadingIndicator()
    }

    CustomSnackBar(snackBarHostState)

}