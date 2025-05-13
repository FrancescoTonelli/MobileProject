package com.hitwaves.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    val contentId: Int,
    val isTour: Boolean,
    val backgroundImageUrl: String,
    val title: String,
    val artist: Artist,
    val description: String,
    val date: String?
    //place
    //address
    //time
) : Parcelable
