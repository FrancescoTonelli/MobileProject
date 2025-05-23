package com.hitwaves.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.api.ApiResult
import com.hitwaves.api.TicketDetailsResponse
import com.hitwaves.api.TicketQrResponse
import com.hitwaves.api.TokenManager
import com.hitwaves.api.getHttpConcertImageUrl
import com.hitwaves.api.getHttpTourImageUrl
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography

@Composable
fun TicketDisplayFuture(details: ApiResult<TicketDetailsResponse>, qr: ApiResult<TicketQrResponse>, loadingQr: Boolean) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(50.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center

            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        if (details.data?.tourTitle.isNullOrEmpty()) {
                            getHttpConcertImageUrl(details.data?.concertImage)
                        } else {
                            getHttpTourImageUrl(details.data?.concertImage)
                        }
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box (
                        modifier = Modifier
                            .background(Primary)
                    ){
                        (if (details.success) {
                            if (details.data?.tourTitle.isNullOrEmpty()) {
                                details.data?.concertTitle
                            } else {
                                "${details.data?.tourTitle} - ${details.data?.concertTitle}"
                            }
                        } else {
                            details.errorMessage.toString()
                        })?.let {
                            Text(
                                text = it,
                                style = Typography.titleLarge.copy(
                                    fontSize = 22.sp,
                                    color = Secondary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (loadingQr) {
                        LoadingIndicator()
                    }
                    else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(FgDark),
                            contentAlignment = Alignment.Center
                        ) {
                            if (qr.success && qr.data != null) {
                                QrCodeView(
                                    userId = qr.data.userId,
                                    ticketId = qr.data.ticketId,
                                    concertId = qr.data.concertId,
                                    qrImageSize = 250.dp
                                )
                            } else {
                                Text(
                                    text = "${TokenManager.getToken()} ${qr.errorMessage.toString()}",
                                    style = Typography.bodyLarge.copy(
                                        fontSize = 20.sp,
                                        color = Secondary
                                    )
                                )
                            }
                        }
                    }

                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            Title(
                title = "Details"
            )

            DetailRow(
                label = "Place",
                value = details.data!!.placeName,
                displayDivider = false
            )
            GmapsDetailRow(
                label = "Address",
                value = details.data.placeAddress,
                displayDivider = false
            )
            DetailRow(
                label = "Date",
                value = details.data.concertDate,
                displayDivider = false
            )
            DetailRow(
                label = "Time",
                value = details.data.concertTime,
                displayDivider = false
            )

            Spacer(modifier = Modifier.height(8.dp))
            Title(
                title = "Your Seat"
            )

            DetailRow(
                label = "Sector",
                value = details.data.sectorName,
                displayDivider = false
            )
            DetailRow(
                label = "Seat",
                value = details.data.seatDescription,
                displayDivider = false
            )
        }
    }
}