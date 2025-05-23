package com.hitwaves.ui.component


import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.Dp
import com.hitwaves.utils.generateQrCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Composable
fun QrCodeView(userId: Int, ticketId: Int, concertId: Int, qrImageSize: Dp = 200.dp ) {
    val qrContent = remember(userId, ticketId, concertId) {
        JSONObject().apply {
            put("user_id", userId)
            put("ticket_id", ticketId)
            put("concert_id", concertId)
        }.toString()
    }

    var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(qrContent) {
        withContext(Dispatchers.Default) {
            qrBitmap = generateQrCode(qrContent)
        }
    }

    if (qrBitmap == null) {
        LoadingIndicator()
    } else {
        Image(
            bitmap = qrBitmap!!.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier.size(qrImageSize)
        )
    }
}