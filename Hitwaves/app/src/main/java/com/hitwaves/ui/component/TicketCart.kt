package com.hitwaves.ui.component

import androidx.compose.runtime.Composable
import com.hitwaves.model.TicketToCheckout

@Composable
fun TicketCart(
    concertId: Int,
    tickets: List<TicketToCheckout>,
    onCheckout: (Int, List<TicketToCheckout>) -> Unit
) {

}