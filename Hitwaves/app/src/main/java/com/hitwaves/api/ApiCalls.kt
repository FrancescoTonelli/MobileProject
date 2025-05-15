package com.hitwaves.api


suspend fun apiAutomaticLogin(): ApiResult<TokenResponse> {
    val url = "automatic_login"
    return HttpHelper.postRequestAsync<TokenResponse>(
        requestData = Unit,
        endpoint = url,
        withAuth = true
    )
}

suspend fun apiLoginUser(loginRequest: LoginRequest): ApiResult<TokenResponse> {
    val url = "login"
    return HttpHelper.postRequestAsync<TokenResponse>(
        requestData = loginRequest,
        endpoint = url,
        withAuth = false
    )
}

suspend fun apiRegisterUser(registerRequest: RegisterRequest): ApiResult<TokenResponse> {
    val url = "register"
    return HttpHelper.postRequestAsync<TokenResponse>(
        requestData = registerRequest,
        endpoint = url,
        withAuth = false
    )
}
//
//suspend fun apiLogoutUser(): Pair<Boolean, Any?> {
//    val url = "logout"
//    return HttpHelper.postRequestAsync<MessageResponse>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiDeleteUser(): Pair<Boolean, Any?> {
//    val url = "delete"
//    return HttpHelper.postRequestAsync<MessageResponse>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetNearestConcerts(positionRequest: PositionRequest): Pair<Boolean, Any?> {
//    val url = "nearest_concerts"
//    return HttpHelper.postRequestAsync<List<NearestConcert>>(
//        requestData = positionRequest,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetPopularArtistsEvents(): Pair<Boolean, Any?> {
//    val url = "popular_artists_events"
//    return HttpHelper.postRequestAsync<List<PopularArtistEvent>>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetArtists(): Pair<Boolean, Any?> {
//    val url = "artists"
//    return HttpHelper.postRequestAsync<List<ArtistResponse>>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetConcertsNoTour(): Pair<Boolean, Any?> {
//    val url = "concerts_no_tour"
//    return HttpHelper.postRequestAsync<List<ConcertNoTourResponse>>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetMapConcerts(): Pair<Boolean, Any?> {
//    val url = "map_concerts"
//    return HttpHelper.postRequestAsync<List<MapConcertResponse>>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetTours(): Pair<Boolean, Any?> {
//    val url = "tours"
//    return HttpHelper.postRequestAsync<List<TourResponse>>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetTourDetails(tourId: Int): Pair<Boolean, Any?> {
//    val url = "tour/$tourId"
//    return HttpHelper.getRequestAsync<TourDetailsResponse>(
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetConcertDetails(concertId: Int): Pair<Boolean, Any?> {
//    val url = "concert/$concertId"
//    return HttpHelper.getRequestAsync<ConcertDetailsResponse>(
//        endpoint = url,
//        withAuth = true
//    )
//}
//
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
//suspend fun apiGetLikedArtists(): Pair<Boolean, Any?> {
//    val url = "liked_artists"
//    return HttpHelper.postRequestAsync<List<LikedArtistResponse>>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiLikeOrUnlikeArtist(artistId: Int): Pair<Boolean, Any?> {
//    val url = "like/$artistId"
//    return HttpHelper.postRequestAsync<String>(
//        requestData = Unit,
//        endpoint = url,
//        withAuth = true
//    )
//}
//
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
//
//suspend fun apiGetUserDetails(): Pair<Boolean, Any?> {
//    val url = "details"
//
//    return HttpHelper.getRequestAsync<UserDetailsResponse>(
//        endpoint = url,
//        withAuth = true
//    )
//}
//
//suspend fun apiUpdateUserDetails(userUpdateRequest: UserUpdateRequest): Pair<Boolean, Any?> {
//    val url = "update"
//
//    val body = mapOf(
//        "name" to userUpdateRequest.name,
//        "surname" to userUpdateRequest.surname,
//        "birthdate" to userUpdateRequest.birthdate,
//        "username" to userUpdateRequest.username,
//        "email" to userUpdateRequest.email,
//        "password" to userUpdateRequest.password
//    )
//
//    return HttpHelper.putRequestAsync<MessageResponse>(
//        endpoint = url,
//        requestData = body,
//        withAuth = true
//    )
//}
//
//suspend fun apiUpdateUserImage(imageData: List<Byte>): Pair<Boolean, Any?> {
//    val endpoint = "update_image"
//    val request = UpdateUserImageRequest(imageData)
//
//    return HttpHelper.putRequestAsync<UpdateUserImageResponse>(
//        requestData = request,
//        endpoint = endpoint,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetUserReviews(): Pair<Boolean, Any?> {
//    val endpoint = "reviews"
//
//    return HttpHelper.getRequestAsync<List<UserReviewResponses>>(
//        endpoint = endpoint,
//        withAuth = true
//    )
//}
//
//suspend fun apiDeleteUserReview(reviewId: Int): Pair<Boolean, Any?> {
//    val endpoint = "review/delete/$reviewId"
//
//    return HttpHelper.deleteRequestAsync<MessageResponse>(
//        endpoint = endpoint,
//        withAuth = true
//    )
//}
//
//suspend fun apiGetAllNotifications(): Pair<Boolean, Any?> {
//    val endpoint = "notifications"
//
//    return HttpHelper.getRequestAsync<List<NotificationResponse>>(
//        endpoint = endpoint,
//        withAuth = true
//    )
//}
//
//suspend fun apiReadNotification(notificationId: Int): Pair<Boolean, Any?> {
//    val endpoint = "notification/read/$notificationId"
//
//    return HttpHelper.postRequestAsync<MessageResponse>(
//        requestData = Unit,
//        endpoint = endpoint,
//        withAuth = true
//    )
//}
