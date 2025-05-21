package com.hitwaves.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


suspend fun apiAutomaticLogin(): ApiResult<TokenResponse> {
    val url = "automatic_login"
    return ApiGenericCalls.postRequestAsync<TokenResponse>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiLoginUser(loginRequest: LoginRequest): ApiResult<TokenResponse> {
    val url = "login"
    return ApiGenericCalls.postRequestAsync<TokenResponse>(
        requestData = loginRequest,
        endpoint = url,
        withAuth = false
    )
}

suspend fun apiRegisterUser(registerRequest: RegisterRequest): ApiResult<TokenResponse> {
    val url = "register"
    return ApiGenericCalls.postRequestAsync<TokenResponse>(
        requestData = registerRequest,
        endpoint = url,
        withAuth = false
    )
}

suspend fun apiLogoutUser(): ApiResult<MessageResponse> {
    val url = "logout"
    return ApiGenericCalls.postRequestAsync<MessageResponse>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiDeleteUser(): ApiResult<MessageResponse> {
    val url = "delete"
    return ApiGenericCalls.deleteRequestAsync<MessageResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiGetNearestConcerts(positionRequest: PositionRequest): ApiResult<List<NearestConcert>> {
    val url = "nearest_concerts"
    return ApiGenericCalls.postRequestAsync<List<NearestConcert>>(
        requestData = positionRequest,
        endpoint = url,
        withAuth = false
    )
}

suspend fun apiGetPopularArtistsEvents(): ApiResult<List<PopularArtistEvent>> {
    val url = "popular_artists_events"
    return ApiGenericCalls.postRequestAsync<List<PopularArtistEvent>>(
        requestData = Unit,
        endpoint = url,
        withAuth = false
    )
}

suspend fun apiGetArtists(): ApiResult<List<ArtistResponse>> {
    val url = "artists"
    return ApiGenericCalls.postRequestAsync<List<ArtistResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiGetConcertsNoTour(): ApiResult<List<ConcertNoTourResponse>> {
    val url = "concerts_no_tour"
    return ApiGenericCalls.postRequestAsync<List<ConcertNoTourResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiGetMapConcerts(): ApiResult<List<MapConcertResponse>> {
    val url = "map_concerts"
    return ApiGenericCalls.postRequestAsync<List<MapConcertResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiGetTours(): ApiResult<List<TourResponse>> {
    val url = "tours"
    return ApiGenericCalls.postRequestAsync<List<TourResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiGetTourDetails(tourId: Int): ApiResult<TourDetailsResponse> {
    val url = "tour/$tourId"
    return ApiGenericCalls.getRequestAsync<TourDetailsResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiGetConcertDetails(concertId: Int): ApiResult<ConcertDetailsResponse> {
    val url = "concert/$concertId"
    return ApiGenericCalls.getRequestAsync<ConcertDetailsResponse>(
        endpoint = url,
        withAuth = true
    )
}

//suspend fun apiGetArtistDetails(artistId: Int): Pair<Boolean, Any?> {
//    val url = "artist/$artistId"
//    return HttpHelper.getRequestAsync<ArtistDetailsResponse>(
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiPurchaseTicket(ticketId: Int): Pair<Boolean, Any?> {
//    val url = "ticket/purchase/$ticketId"
//    return HttpHelper.postRequestAsync<MessageResponse>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
suspend fun apiGetLikedArtists(): ApiResult<List<LikedArtistResponse>> {
    val url = "liked_artists"
    return ApiGenericCalls.getRequestAsync<List<LikedArtistResponse>>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiLikeOrUnlikeArtist(artistId: Int): ApiResult<String> {
    val url = "like/$artistId"
    return ApiGenericCalls.postRequestAsync<String>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

//suspend fun apiGetUserTickets(): Pair<Boolean, Any?> {
//    val url = "tickets"
//    return HttpHelper.getRequestAsync<List<TicketResponse>>(
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiCheckUserReview(concertId: Int): Pair<Boolean, Any?> {
//    val url = "review/check/$concertId"
//    return HttpHelper.getRequestAsync<CheckReviewResponse>(
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiAddReview(reviewRequest: ReviewRequest): Pair<Boolean, Any?> {
//    val url = "review"
//
//    return HttpHelper.postRequestAsync<MessageResponse>(
//        endpoint = url,
//        requestData = reviewRequest,
//        withAuth = true
//    )
//}

suspend fun apiGetUserDetails(): ApiResult<UserDetailsResponse> {
    val url = "details"

    return ApiGenericCalls.getRequestAsync<UserDetailsResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiUpdateUserDetails(userUpdateRequest: UserUpdateRequest): ApiResult<MessageResponse> {
    val url = "update"

    val body = mapOf(
        "name" to userUpdateRequest.name,
        "surname" to userUpdateRequest.surname,
        "birthdate" to userUpdateRequest.birthdate,
        "username" to userUpdateRequest.username,
        "email" to userUpdateRequest.email,
        "password" to userUpdateRequest.password
    )

    return ApiGenericCalls.putRequestAsync<MessageResponse>(
        endpoint = url,
        requestData = body,
        withAuth = true
    )
}

suspend fun updateUserImageRequest(
    imageBytes: ByteArray,
    fileName: String = "profile.jpg"
): ApiResult<UpdateUserImageResponse> = withContext(Dispatchers.IO) {
    try {
        val fullUrl = baseApiUrl + "update_image"

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                fileName,
                imageBytes.toRequestBody("image/*".toMediaType())
            )
            .build()

        val requestBuilder = Request.Builder()
            .url(fullUrl)
            .put(requestBody)

        TokenManager.getToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()
        val response = ApiGenericCalls.client.newCall(request).execute()
        val body = response.body?.string()

        if (response.isSuccessful && body != null) {
            val result = ApiGenericCalls.gson.fromJson(body, UpdateUserImageResponse::class.java)
            ApiResult(success = true, data = result)
        } else {
            val errorMsg = parseErrorMessage(body)
            ApiResult(success = false, errorMessage = errorMsg)
        }
    } catch (e: Exception) {
        ApiResult(success = false, errorMessage = "Network error: ${e.message}")
    }
}


suspend fun apiGetUserReviews(): ApiResult<List<UserReviewResponses>> {
    val endpoint = "reviews"

    return ApiGenericCalls.getRequestAsync<List<UserReviewResponses>>(
        endpoint = endpoint,
        withAuth = true
    )
}

suspend fun apiDeleteUserReview(reviewId: Int): ApiResult<MessageResponse> {
    val endpoint = "review/delete/$reviewId"

    return ApiGenericCalls.deleteRequestAsync<MessageResponse>(
        endpoint = endpoint,
        withAuth = true
    )
}

suspend fun apiGetAllNotifications(): ApiResult<List<NotificationResponse>> {
    val endpoint = "notifications"

    return ApiGenericCalls.getRequestAsync<List<NotificationResponse>>(
        endpoint = endpoint,
        withAuth = true
    )
}

suspend fun apiReadNotification(notificationId: Int): ApiResult<MessageResponse> {
    val endpoint = "notification/read/$notificationId"

    return ApiGenericCalls.postRequestAsync<MessageResponse>(
        requestData = Unit,
        endpoint = endpoint,
        withAuth = true
    )
}
