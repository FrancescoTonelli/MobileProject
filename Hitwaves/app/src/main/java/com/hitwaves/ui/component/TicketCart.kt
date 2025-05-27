package com.hitwaves.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.R
import com.hitwaves.model.TicketToCheckout
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography
import java.util.Locale

@Composable
fun TicketCart(
    tickets: List<TicketToCheckout>,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        if (tickets.isEmpty()) {
            Text("No tickets in cart.")
        } else {
            LazyColumn (
                modifier = Modifier
                    .fillMaxHeight(0.7f)
            ) {
                items(tickets) { ticket ->
                    TicketItem(ticket)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ButtonWithIcons(
                ImageVector.vectorResource(R.drawable.card),
                "Checkout",
                ImageVector.vectorResource(R.drawable.arrow),
                onClickAction = onCheckout
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TicketItem(ticket: TicketToCheckout) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Primary, shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text("Sector: ${ticket.sectorName}", style = Typography.labelSmall.copy(
                fontSize = 16.sp,
                color = Secondary
            ))
            Text("Seat: ${ticket.seatDescription}", style = Typography.labelSmall.copy(
                fontSize = 16.sp,
                color = Secondary
            ))
            Text("Price: € ${ String.format(Locale.US, "%.2f", ticket.price) }", style = Typography.labelSmall.copy(
                fontSize = 16.sp,
                color = Secondary
            ))
        }
    }
}