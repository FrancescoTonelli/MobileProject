package com.hitwaves.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

val spacerHeight = 16.dp

@Composable
fun DetailRow(label: String, value: String, displayDivider: Boolean = true) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(spacerHeight))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = label,
                    style = Typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Start,
                    softWrap = true
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = value,
                    style = Typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = Secondary,
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.End,
                    softWrap = true
                )
            }
        }

        Spacer(modifier = Modifier.height(spacerHeight))

        if (displayDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Secondary
            )
        }
    }
}

@Composable
fun GmapsDetailRow(label: String, value: String, displayDivider: Boolean = true) {
    val context = LocalContext.current
    val encodedAddress = Uri.encode(value)
    val gmapsUri = "https://www.google.com/maps/search/?api=1&query=$encodedAddress"

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(spacerHeight))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = label,
                    style = Typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = Secondary,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Start,
                    softWrap = true
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = value,
                    style = Typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = Secondary,
                        fontWeight = FontWeight.Normal,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gmapsUri))
                        context.startActivity(intent)
                    },
                    textAlign = TextAlign.End,
                    softWrap = true
                )
            }
        }

        Spacer(modifier = Modifier.height(spacerHeight))

        if (displayDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Secondary
            )
        }
    }
}