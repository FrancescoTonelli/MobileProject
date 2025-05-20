package com.hitwaves.api

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("token") val token: String
)

data class MessageResponse(
    @SerializedName("message") val message: String
)

data class NearestConcert(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("tour_title") val tourTitle: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("artist") val artist: String,
    @SerializedName("artist_image") val artistImage: String?,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("date") val date: String,
    @SerializedName("distance") val distance: Double
)

data class PopularArtistEvent(
    @SerializedName("id") val id: Int,
    @SerializedName("isTour") val isTour: Boolean,
    @SerializedName("title") val title: String,
    @SerializedName("image") val image: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("artistImage") val artistImage: String,
    @SerializedName("placeName") val placeName: String,
    @SerializedName("date") val date: String?,
    @SerializedName("concertCount") val concertCount: Int
)


data class ArtistResponse(
    @SerializedName("artist_id") val id: Int,
    @SerializedName("artist_name") val name: String,
    @SerializedName("artist_image") val image: String?,
    @SerializedName("likes_count") val likesCount: Int,
    @SerializedName("average_rating") val averageRating: Float
)

data class ConcertNoTourResponse(
    @SerializedName("concert_id") val id: Int,
    @SerializedName("concert_title") val title: String,
    @SerializedName("concert_image") val image: String?,
    @SerializedName("concert_date") val date: String,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("artist_image") val artistImage: String?
)

//data class MapConcertResponse(
//    @SerializedName("concert_id") val concertId: Int,
//    @SerializedName("artist_image") val artistImage: String?,
//    @SerializedName("place_latitude") val latitude: Double,
//    @SerializedName("place_longitude") val longitude: Double
//)
//
data class TourResponse(
    @SerializedName("tour_id") val tourId: Int,
    @SerializedName("tour_title") val tourTitle: String,
    @SerializedName("tour_image") val tourImage: String?,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("artist_image") val artistImage: String?,
    @SerializedName("concert_count") val concertCount: Int,
    @SerializedName("upcoming_concerts") val upcomingConcerts: Int
)

data class TourArtistResponse(
    @SerializedName("artist_id") val artistId: Int,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("artist_image") val artistImage: String?
)

data class TourConcertResponse(
    @SerializedName("concert_id") val concertId: Int,
    @SerializedName("concert_title") val concertTitle: String,
    @SerializedName("concert_image") val concertImage: String?,
    @SerializedName("concert_date") val concertDate: String,
    @SerializedName("concert_time_str") val concertTimeStr: String,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("place_address") val placeAddress: String,
    @SerializedName("place_latitude") val placeLatitude: Double,
    @SerializedName("place_longitude") val placeLongitude: Double
)

data class TourDetailsResponse(
    @SerializedName("artists") val artists: List<TourArtistResponse>,
    @SerializedName("concerts") val concerts: List<TourConcertResponse>
)

data class ConcertDetailsResponse(
    @SerializedName("concert_info") val concertInfo: ConcertInfoResponse,
    @SerializedName("artists") val artists: List<ArtistConcertDetailsResponse>,
    @SerializedName("available_tickets") val availableTickets: List<TicketConcertDetailsResponse>,
    @SerializedName("sectors") val sectors: List<SectorConcertDetailsResponse>,
    @SerializedName("tour_info") val tourInfo: TourConcertDetailsResponse? = null
)

data class ConcertInfoResponse(
    @SerializedName("concert_title") val concertTitle: String,
    @SerializedName("concert_image") val concertImage: String?,
    @SerializedName("concert_date") val concertDate: String,
    @SerializedName("concert_time") val concertTime: String,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("place_address") val placeAddress: String,
    @SerializedName("place_id") val placeId: Int,
    @SerializedName("tour_id") val tourId: Int?,
    @SerializedName("tour_title") val tourTitle: String?,
    @SerializedName("tour_image") val tourImage: String?,
    @SerializedName("effective_image") val effectiveImage: String,
    @SerializedName("is_part_of_tour") val isPartOfTour: Boolean
)

data class ArtistConcertDetailsResponse(
    @SerializedName("artist_id") val artistId: Int,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("artist_image") val artistImage: String?
)

data class TicketConcertDetailsResponse(
    @SerializedName("ticket_id") val ticketId: Int,
    @SerializedName("ticket_price") val ticketPrice: Double,
    @SerializedName("sector_id") val sectorId: Int,
    @SerializedName("tour_name") val tourName: String,
    @SerializedName("sector_is_stage") val sectorIsStage: Boolean,
    val xSx: Double?,
    val ySx: Double?,
    val xDx: Double?,
    val yDx: Double?,
    @SerializedName("seat_id") val seatId: Int,
    @SerializedName("seat_description") val seatDescription: String?,
    val x: Double?,
    val y: Double?
)

data class SectorConcertDetailsResponse(
    val id: Int,
    val name: String,
    @SerializedName("is_stage") val isStage: Int,
    @SerializedName("x_sx") val xSx: Double?,
    @SerializedName("y_sx") val ySx: Double?,
    @SerializedName("x_dx") val xDx: Double?,
    @SerializedName("y_dx") val yDx: Double?
)

data class TourConcertDetailsResponse(
    val id: Int,
    val title: String,
    val image: String?
)

//data class ArtistDetailsResponse(
//    val artist: ArtistDetailsInfoResponse,
//    val concerts: List<ConcertArtistDetailsResponse>,
//    val tours: List<TourArtistDetailsResponse>,
//    val reviews: List<ReviewArtistDetailsResponse>
//)
//
//data class ArtistDetailsInfoResponse(
//    val id: Int,
//    val name: String,
//    val image: String?,
//    @SerializedName("likes_count") val likesCount: Int,
//    @SerializedName("average_rating") val averageRating: Double,
//    @SerializedName("is_liked") val isLiked: Boolean
//)
//
//data class ConcertArtistDetailsResponse(
//    val id: Int,
//    val title: String,
//    val image: String?,
//    val date: String,
//    @SerializedName("place_name") val placeName: String
//)
//
//data class TourArtistDetailsResponse(
//    val id: Int,
//    val title: String,
//    val image: String?,
//    @SerializedName("concerts_count") val concertsCount: Int
//)
//
//data class ReviewArtistDetailsResponse(
//    val id: Int,
//    val rate: Double,
//    val description: String?,
//    @SerializedName("concert_title") val concertTitle: String,
//    @SerializedName("concert_date") val concertDate: String,
//    val username: String,
//    @SerializedName("user_image") val userImage: String?
//)

data class LikedArtistResponse(
    val id: Int,
    val name: String,
    val image: String?,
    @SerializedName("likes_count") val likesCount: Int,
    @SerializedName("average_rating") val averageRating: Float
)
//
//data class TicketResponse(
//    @SerializedName("likes_count") val ticketId: Int,
//    val concert: ConcertTicketResponse
//)
//
//data class ConcertTicketResponse(
//    val id: Int,
//    val title: String,
//    val image: String?,
//    val date: String,
//    val time: String,
//    @SerializedName("place_name")  val placeName: String,
//    val artist: ArtistTicketResponse,
//    val tour: TourTicketResponse?
//)
//
//data class ArtistTicketResponse(
//    val id: Int,
//    val name: String,
//    val image: String?
//)
//
//data class TourTicketResponse(
//    val id: Int,
//    val title: String,
//    val image: String?
//)
//
//data class CheckReviewResponse(
//    @SerializedName("has_reviewed") val hasReviewed: Boolean,
//    val review: ReviewCheckReviewResponse? = null
//)
//
//data class ReviewCheckReviewResponse(
//    val id: Int,
//    val rate: Float,
//    val description: String
//)

data class UserDetailsResponse(
    val id: Int,
    val username: String,
    val email: String,
    val name: String,
    val surname: String,
    val birthdate: String,
    val refunds: Double,
    val image: String?
)

data class UpdateUserImageResponse(
    val message: String,
    @SerializedName("image_path") val imagePath: String
)

data class UserReviewResponses(
    @SerializedName("review_id") val reviewId: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("concert_title") val concertTitle: String,
    @SerializedName("concert_date") val concertDate: String,
    @SerializedName("artist_name") val artistName: String,
    @SerializedName("artist_image") val artistImage: String? = null
)

data class NotificationResponse(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("is_read") val isRead: Int
)