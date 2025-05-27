package com.hitwaves.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataToCheckout(
    val concertId: Int,
    val tickets: List<TicketToCheckout>
) : Parcelable

@Parcelize
data class TicketToCheckout (
    val ticketId: Int,
    val sectorName: String,
    val seatDescription: String,
    val price: Double
): Parcelable