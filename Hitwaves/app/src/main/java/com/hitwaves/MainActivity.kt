package com.hitwaves

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hitwaves.ui.theme.HitwavesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.runtime.*
import kotlinx.coroutines.launch


suspend fun postToFlask(): String {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val requestBody = "".toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://192.168.187.162:5000/protected_user/artists") // Cambia IP con quello del tuo PC
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw Exception("Errore: ${response.code}")
        response.body?.string() ?: throw Exception("Risposta vuota")
    }
}

@Composable
fun ArtistFetcher() {
    val scope = rememberCoroutineScope()
    var response by remember { mutableStateOf("Attendi risposta...") }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                response = postToFlask()
            } catch (e: Exception) {
                response = "Errore: ${e.message}"
            }
        }
    }

    Text(text = response)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HitwavesTheme {
                ArtistFetcher()
            }
        }
    }
}