package com.hitwaves.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Artist(
    val artistId: Int,
    val artistName: String,
    val artistImageUrl: String?,
    val likesCount: Int,
    val averageRating: Float,
    val isLiked: Boolean = false
) : Parcelable
