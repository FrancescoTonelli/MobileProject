package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.api.CanvasResponse
import com.hitwaves.model.DataToCheckout
import com.hitwaves.model.TicketToCheckout
import com.hitwaves.ui.component.ButtonWithIcons
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.SeatChart
import com.hitwaves.ui.component.TicketCart
import com.hitwaves.ui.theme.BgDark
import com.hitwaves.ui.viewModel.ConcertViewModel

private fun init(): ConcertViewModel {
    return ConcertViewModel()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConcertCanvas(navController: NavHostController, concertId: Int) {
    val concertViewModel = remember { init() }
    val canvasData by concertViewModel.canvasState
    val isLoadingCanvas by concertViewModel.isLoadingCanvas

    val snackBarHostState = remember { SnackbarHostState() }

    val ticketCart = remember { mutableStateListOf<Int>() }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        concertViewModel.getConcertCanvas(concertId)
    }

    LaunchedEffect(canvasData) {
        if (!isLoadingCanvas && canvasData.errorMessage != null) {
            snackBarHostState.showSnackbar(canvasData.errorMessage!!)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (canvasData.success && canvasData.data != null) {
            SeatChart(
                tickets = canvasData.data?.tickets ?: emptyList(),
                sectors = canvasData.data?.sectors ?: emptyList(),
                onTicketClick = { ticket ->
                    if (ticket.ticketUserId == null) {
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
        } else if (!isLoadingCanvas && canvasData.errorMessage != null) {
            Text("Error: ${canvasData.errorMessage}")
        }

        if (ticketCart.isNotEmpty()) {

            ButtonWithIcons(
                startIcon = ImageVector.vectorResource(R.drawable.cart),
                textBtn = "Cart (${ticketCart.size})",
                onClickAction = {
                    showBottomSheet = true
                },
                modifier = Modifier
                    .padding(8.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(20.dp))
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = bottomSheetState,
            containerColor = BgDark
        ) {
            TicketCart(
                tickets = showTickets(ticketCart, canvasData),
                onCheckout = {
                    val checkoutData = DataToCheckout(
                        concertId = concertId,
                        tickets = showTickets(ticketCart, canvasData)
                    )

                    navController.currentBackStackEntry?.savedStateHandle?.set("checkoutData", checkoutData)

                    navController.navigate("checkout")
                    showBottomSheet = false
                }
            )
        }
    }

    if (isLoadingCanvas) LoadingIndicator()

    CustomSnackBar(snackBarHostState)

    GoBack(navController)


}

fun showTickets(
    tickets: List<Int>,
    canvasData: ApiResult<CanvasResponse>
) : List<TicketToCheckout>{
    val ticketList = mutableListOf<TicketToCheckout>()

    for (ticketId in tickets) {
        ticketList.add(
            TicketToCheckout(
                ticketId = ticketId,
                sectorName = canvasData.data?.tickets?.find { it.ticketId == ticketId }?.sectorName ?: "Unknown Sector",
                seatDescription = canvasData.data?.tickets?.find { it.ticketId == ticketId }?.seatDescription ?: "Unknown Seat",
                price = canvasData.data?.tickets?.find { it.ticketId == ticketId }?.ticketPrice ?: 0.0
            )
        )
    }

    return ticketList
}