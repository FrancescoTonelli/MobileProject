package com.hitwaves.api

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import android.app.AlertDialog
import android.content.Context

const val baseUrl = "http://192.168.187.162:5000/protected_user/"

fun errorDisplay(context: Context, msg: String) {
    AlertDialog.Builder(context)
        .setTitle("Error")
        .setMessage(msg)
        .setPositiveButton("OK", null)
        .show()
}

object HttpHelper {
    val client = OkHttpClient()
    val gson = Gson()

    suspend inline fun <reified T : Any> postRequestAsync(
        requestData: Any,
        endpoint: String,
        withAuth: Boolean = false
    ): Pair<Boolean, Any?> = withContext(Dispatchers.IO) {
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
                val token = TokenManager.getToken()
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
                    return@withContext Pair(true, result)
                } catch (e: JsonSyntaxException) {
                    return@withContext Pair(false, "Response parsing error: ${e.message}")
                }
            } else {
                return@withContext Pair(false, "HTTP error ${response.code}: ${body ?: "No response"}")
            }
        } catch (e: Exception) {
            return@withContext Pair(false, "Net error: ${e.message}")
        }
    }

    suspend inline fun <reified T : Any> getRequestAsync(
        endpoint: String,
        withAuth: Boolean = false
    ): Pair<Boolean, Any?> = withContext(Dispatchers.IO) {
        try {
            val fullUrl = baseUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .get()

            if (withAuth) {
                val token = TokenManager.getToken()
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
                    return@withContext Pair(true, result)
                } catch (e: JsonSyntaxException) {
                    return@withContext Pair(false, "Response parsing error: ${e.message}")
                }
            } else {
                return@withContext Pair(false, "HTTP error ${response.code}: ${body ?: "No response"}")
            }
        } catch (e: Exception) {
            return@withContext Pair(false, "Net error: ${e.message}")
        }
    }

    suspend inline fun <reified T : Any> putRequestAsync(
        requestData: Any,
        endpoint: String,
        withAuth: Boolean = false
    ): Pair<Boolean, Any?> = withContext(Dispatchers.IO) {
        try {
            val json = gson.toJson(requestData)
            val mediaType = "application/json".toMediaType()
            val requestBody = json.toRequestBody(mediaType)

            val fullUrl = baseUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .put(requestBody)  // Usa il metodo PUT
                .addHeader("Content-Type", "application/json")

            if (withAuth) {
                val token = TokenManager.getToken()
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
                    return@withContext Pair(true, result)
                } catch (e: JsonSyntaxException) {
                    return@withContext Pair(false, "Response parsing error: ${e.message}")
                }
            } else {
                return@withContext Pair(false, "HTTP error ${response.code}: ${body ?: "No response"}")
            }
        } catch (e: Exception) {
            return@withContext Pair(false, "Net error: ${e.message}")
        }
    }

    suspend inline fun <reified T : Any> deleteRequestAsync(
        endpoint: String,
        withAuth: Boolean = false
    ): Pair<Boolean, Any?> = withContext(Dispatchers.IO) {
        try {
            val fullUrl = baseUrl.trimEnd('/') + "/" + endpoint.trimStart('/')

            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .delete()

            if (withAuth) {
                val token = TokenManager.getToken()
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            }

            val request = requestBuilder.build()
            val response = client.newCall(request).execute()
            val body = response.body?.string()

            if (response.isSuccessful && body != null) {
                try {
                    val result = gson.fromJson(body, T::class.java)
                    return@withContext Pair(true, result)
                } catch (e: JsonSyntaxException) {
                    return@withContext Pair(false, "Response parsing error: ${e.message}")
                }
            } else {
                return@withContext Pair(false, "HTTP error ${response.code}: ${body ?: "No response"}")
            }
        } catch (e: Exception) {
            return@withContext Pair(false, "Net error: ${e.message}")
        }
    }

}
