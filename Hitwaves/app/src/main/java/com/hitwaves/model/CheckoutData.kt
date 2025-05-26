package com.hitwaves.model

data class CheckoutData(
    val concertId: Int,
    val tickets: List<TicketToCheckout>
)

data class TicketToCheckout (
    val ticketId: Int,
    val sectorName: String,
    val seatDescription: String,
    val price: Float
)