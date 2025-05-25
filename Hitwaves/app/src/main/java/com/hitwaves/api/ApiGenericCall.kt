package com.hitwaves.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

const val baseUrl = "http://192.168.33.249:5000/"
const val baseApiUrl = "${baseUrl}protected_user/"
const val artistImageUrl = "${baseUrl}static/images/artists/"
const val concertImageUrl = "${baseUrl}static/images/concerts/"
const val tourImageUrl = "${baseUrl}static/images/tours/"
const val userImageUrl = "${baseUrl}static/images/users/"

fun getHttpArtistImageUrl(artistImageName: String?): String {
    return if (!artistImageName.isNullOrEmpty()) {
        "$artistImageUrl$artistImageName"
    } else {
        artistImageUrl + "default.png"
    }
}

fun getHttpConcertImageUrl(concertImageName: String?): String {
    return if (!concertImageName.isNullOrEmpty()) {
        "$concertImageUrl$concertImageName"
    } else {
        concertImageUrl + "default.png"
    }
}

fun getHttpTourImageUrl(tourImageName: String?): String {
    return if (!tourImageName.isNullOrEmpty()) {
        "$tourImageUrl$tourImageName"
    } else {
        tourImageUrl + "default.png"
    }
}

fun getHttpUserImageUrl(userImageName: String?): String {
    return if (!userImageName.isNullOrEmpty()) {
        "$userImageUrl$userImageName"
    } else {
        userImageUrl + "default.png"
    }
}

fun parseErrorMessage(body: String?): String {
    return try {
        Gson().fromJson(body, Map::class.java)["message"]?.toString() ?: "Unknown error"
    } catch (e: Exception) {
        body ?: "Unknown error"
    }
}

object ApiGenericCalls {
    val client = OkHttpClient()
    val gson = Gson()

    suspend inline fun <reified T : Any> getRequestAsync(
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> = makeRequest("GET", endpoint, null, withAuth)

    suspend inline fun <reified T : Any> postRequestAsync(
        requestData: Any,
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> {
        val json = gson.toJson(requestData)
        val mediaType = "application/json".toMediaType()
        val body = json.toRequestBody(mediaType)
        return makeRequest("POST", endpoint, body, withAuth)
    }

    suspend inline fun <reified T : Any> putRequestAsync(
        requestData: Any,
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> {
        val json = gson.toJson(requestData)
        val mediaType = "application/json".toMediaType()
        val body = json.toRequestBody(mediaType)
        return makeRequest("PUT", endpoint, body, withAuth)
    }

    suspend inline fun <reified T : Any> deleteRequestAsync(
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> = makeRequest("DELETE", endpoint, null, withAuth)

    // ✅ Common internal request method
    suspend inline fun <reified T : Any> makeRequest(
        method: String,
        endpoint: String,
        requestBody: okhttp3.RequestBody? = null,
        withAuth: Boolean = false
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val fullUrl = baseApiUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val builder = Request.Builder().url(fullUrl)

            when (method) {
                "GET" -> builder.get()
                "POST" -> builder.post(requestBody!!)
                "PUT" -> builder.put(requestBody!!)
                "DELETE" -> builder.delete()
            }

            if (withAuth) {
                TokenManager.getToken()?.let {
                    builder.addHeader("Authorization", "Bearer $it")
                }
            }

            val request = builder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val type = object : TypeToken<T>() {}.type
                    val result: T = gson.fromJson(body, type)
                    ApiResult(success = true, data = result)
                } catch (e: JsonSyntaxException) {
                    ApiResult(success = false, errorMessage = "Parsing error: ${e.message}")
                }
            } else {
                val errorMsg = parseErrorMessage(body)
                ApiResult(success = false, errorMessage = errorMsg)
            }
        } catch (e: Exception) {
            ApiResult(success = false, errorMessage = "Network error: ${e.message}")
        }
    }
}
