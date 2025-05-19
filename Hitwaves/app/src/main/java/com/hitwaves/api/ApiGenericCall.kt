package com.hitwaves.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

const val baseUrl = "http://192.168.162.63:5000/protected_user/"

fun parseErrorMessage(body: String?): String {
    return try {
        Gson().fromJson(body, Map::class.java)["message"]?.toString() ?: "Unknown error"
    } catch (e: Exception) {
        body ?: "Unknown error"
    }
}

object HttpHelper {
    val client = OkHttpClient()
    val gson = Gson()

    suspend inline fun <reified T : Any> postRequestAsync(
        requestData: Any,
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val json = gson.toJson(requestData)
            val mediaType = "application/json".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val fullUrl = baseUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")

            if (withAuth) {
                TokenManager.getToken()?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
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

    suspend inline fun <reified T : Any> getRequestAsync(
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val fullUrl = baseUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .get()

            if (withAuth) {
                TokenManager.getToken()?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
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

    suspend inline fun <reified T : Any> putRequestAsync(
        requestData: Any,
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val json = gson.toJson(requestData)
            val mediaType = "application/json".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val fullUrl = baseUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .put(requestBody)
                .addHeader("Content-Type", "application/json")

            if (withAuth) {
                TokenManager.getToken()?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
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

    suspend inline fun <reified T : Any> deleteRequestAsync(
        endpoint: String,
        withAuth: Boolean = false
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val fullUrl = baseUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .delete()

            if (withAuth) {
                TokenManager.getToken()?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
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
