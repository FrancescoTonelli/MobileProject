package com.hitwaves.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.EncodeHintType

fun generateQrCode(content: String, size: Int = 512): Bitmap {
    val hints = mapOf(
        EncodeHintType.CHARACTER_SET to "UTF-8"
    )

    val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

    for (x in 0 until size) {
        for (y in 0 until size) {
            val isSet = bitMatrix[x, y]
            val pixelColor = if (isSet) android.graphics.Color.WHITE else android.graphics.Color.TRANSPARENT
            bmp.setPixel(x, y, pixelColor)
        }
    }

    return bmp
}