package com.hitwaves.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventForCards(
    // Logic
    val contentId: Int,
    val isTour: Boolean,
    val placeName: String?,
    val isTicket: Boolean = false,

    // Display
    val title: String,
    val backgroundImage: String,
    val artistName: String,
    val artistImage: String,
    val description: String?,
    val date: String?
) : Parcelable
