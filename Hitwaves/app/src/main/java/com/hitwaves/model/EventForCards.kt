package com.hitwaves.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventForCards(
    val contentId: Int,
    val isTour: Boolean,
    val title: String,
    val backgroundImage: String,
    val artistName: String,
    val artistImage: String,
    val description: String?,
    val date: String?
) : Parcelable
