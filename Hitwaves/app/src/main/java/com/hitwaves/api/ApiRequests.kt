package com.hitwaves.api


data class LoginRequest(
    val email: String? = null,
    val username: String? = null,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val surname: String,
    val birthdate: String,
    val username: String,
    val email: String,
    val password: String
)

data class PositionRequest(
    val latitude: Double,
    val longitude: Double
)

//data class ReviewRequest(
//    @SerializedName("ticket_id") val ticketId: Int,
//    val rate: Int,
//    val description: String? = ""
//)

data class UserUpdateRequest(
    val name: String?,
    val surname: String?,
    val birthdate: String?,
    val username: String?,
    val email: String?,
    val password: String?
)

data class UpdateUserImageRequest(
    val image: List<Byte>
)