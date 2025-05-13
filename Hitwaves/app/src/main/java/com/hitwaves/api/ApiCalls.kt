package com.hitwaves.api

suspend fun loginUser(loginRequest: LoginRequest): Pair<Boolean, Any?> {
    val url = "login"
    return HttpHelper.postRequestAsync<TokenResponse>(
        requestData = loginRequest,
        endpoint = url,
        withAuth = false
    )
}

suspend fun registerUser(registerRequest: RegisterRequest): Pair<Boolean, Any?> {
    val url = "register"
    return HttpHelper.postRequestAsync<TokenResponse>(
        requestData = registerRequest,
        endpoint = url,
        withAuth = false
    )
}

suspend fun logoutUser(): Pair<Boolean, Any?> {
    val url = "logout"
    return HttpHelper.postRequestAsync<MessageResponse>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun deleteUser(): Pair<Boolean, Any?> {
    val url = "delete"
    return HttpHelper.postRequestAsync<MessageResponse>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun automaticLogin(): Pair<Boolean, Any?> {
    val url = "automatic_login"
    return HttpHelper.postRequestAsync<MessageResponse>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getNearestConcerts(positionRequest: PositionRequest): Pair<Boolean, Any?> {
    val url = "nearest_concerts"
    return HttpHelper.postRequestAsync<List<NearestConcert>>(
        requestData = positionRequest,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getPopularArtistsEvents(): Pair<Boolean, Any?> {
    val url = "popular_artists_events"
    return HttpHelper.postRequestAsync<List<PopularArtistEvent>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getArtists(): Pair<Boolean, Any?> {
    val url = "artists"
    return HttpHelper.postRequestAsync<List<ArtistResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getConcertsNoTour(): Pair<Boolean, Any?> {
    val url = "concerts_no_tour"
    return HttpHelper.postRequestAsync<List<ConcertNoTourResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getMapConcerts(): Pair<Boolean, Any?> {
    val url = "map_concerts"
    return HttpHelper.postRequestAsync<List<MapConcertResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getTours(): Pair<Boolean, Any?> {
    val url = "tours"
    return HttpHelper.postRequestAsync<List<TourResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getTourDetails(tourId: Int): Pair<Boolean, Any?> {
    val url = "tour/$tourId"
    return HttpHelper.getRequestAsync<TourDetailsResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun getConcertDetails(concertId: Int): Pair<Boolean, Any?> {
    val url = "concert/$concertId"
    return HttpHelper.getRequestAsync<ConcertDetailsResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun getArtistDetails(artistId: Int): Pair<Boolean, Any?> {
    val url = "artist/$artistId"
    return HttpHelper.getRequestAsync<ArtistDetailsResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun purchaseTicket(ticketId: Int): Pair<Boolean, Any?> {
    val url = "ticket/purchase/$ticketId"
    return HttpHelper.postRequestAsync<MessageResponse>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getLikedArtists(): Pair<Boolean, Any?> {
    val url = "liked_artists"
    return HttpHelper.postRequestAsync<List<LikedArtistResponse>>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun likeOrUnlikeArtist(artistId: Int): Pair<Boolean, Any?> {
    val url = "like/$artistId"
    return HttpHelper.postRequestAsync<String>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun getUserTickets(): Pair<Boolean, Any?> {
    val url = "tickets"
    return HttpHelper.getRequestAsync<List<TicketResponse>>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun checkUserReview(concertId: Int): Pair<Boolean, Any?> {
    val url = "review/check/$concertId"
    return HttpHelper.getRequestAsync<CheckReviewResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun addReview(reviewRequest: ReviewRequest): Pair<Boolean, Any?> {
    val url = "review"

    return HttpHelper.postRequestAsync<MessageResponse>(
        endpoint = url,
        requestData = reviewRequest,
        withAuth = true
    )
}

suspend fun getUserDetails(): Pair<Boolean, Any?> {
    val url = "details"

    return HttpHelper.getRequestAsync<UserDetailsResponse>(
        endpoint = url,
        withAuth = true
    )
}

suspend fun updateUserDetails(userUpdateRequest: UserUpdateRequest): Pair<Boolean, Any?> {
    val url = "update"

    val body = mapOf(
        "name" to userUpdateRequest.name,
        "surname" to userUpdateRequest.surname,
        "birthdate" to userUpdateRequest.birthdate,
        "username" to userUpdateRequest.username,
        "email" to userUpdateRequest.email,
        "password" to userUpdateRequest.password
    )

    return HttpHelper.putRequestAsync<MessageResponse>(
        endpoint = url,
        requestData = body,
        withAuth = true
    )
}

suspend fun updateUserImage(imageData: List<Byte>): Pair<Boolean, Any?> {
    val endpoint = "update_image"
    val request = UpdateUserImageRequest(imageData)

    return HttpHelper.putRequestAsync<UpdateUserImageResponse>(
        requestData = request,
        endpoint = endpoint,
        withAuth = true
    )
}

suspend fun getUserReviews(): Pair<Boolean, Any?> {
    val endpoint = "reviews"

    return HttpHelper.getRequestAsync<List<UserReviewResponses>>(
        endpoint = endpoint,
        withAuth = true
    )
}

suspend fun deleteUserReview(reviewId: Int): Pair<Boolean, Any?> {
    val endpoint = "review/delete/$reviewId"

    return HttpHelper.deleteRequestAsync<MessageResponse>(
        endpoint = endpoint,
        withAuth = true
    )
}

suspend fun getAllNotifications(): Pair<Boolean, Any?> {
    val endpoint = "notifications"

    return HttpHelper.getRequestAsync<List<NotificationResponse>>(
        endpoint = endpoint,
        withAuth = true
    )
}

suspend fun readNotification(notificationId: Int): Pair<Boolean, Any?> {
    val endpoint = "notification/read/$notificationId"

    return HttpHelper.postRequestAsync<MessageResponse>(
        requestData = Unit,
        endpoint = endpoint,
        withAuth = true
    )
}
