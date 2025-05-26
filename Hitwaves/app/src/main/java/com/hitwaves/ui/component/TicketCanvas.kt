package com.hitwaves.ui.component

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.hitwaves.api.SectorCanvasResponse
import com.hitwaves.api.TicketCanvasResponse
import com.hitwaves.ui.theme.*

@Composable
fun InteractiveCanvasMap(
    tickets: List<TicketCanvasResponse>,
    sectors: List<SectorCanvasResponse>,
    onTicketClick: (TicketCanvasResponse) -> Unit,
    checkInCart: (Int) -> Boolean
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isInitialized by remember { mutableStateOf(false) }

    val allPoints = remember(sectors, tickets) {
        sectors.flatMap {
            listOf(
                Offset(it.xSx, it.ySx),
                Offset(it.xDx, it.yDx)
            )
        } + tickets.map { Offset(it.seatX, it.seatY) }
    }

    val minX = allPoints.minOfOrNull { it.x } ?: 0f
    val maxX = allPoints.maxOfOrNull { it.x } ?: 0f
    val minY = allPoints.minOfOrNull { it.y } ?: 0f
    val maxY = allPoints.maxOfOrNull { it.y } ?: 0f

    val contentCenter = Offset((minX + maxX) / 2f, (minY + maxY) / 2f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Secondary)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale *= zoom
                    offset += pan
                }
            }
            .pointerInput(tickets, scale, offset) {
                detectTapGestures { tap ->
                    val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                    val logicalTap = (tap - offset - canvasCenter) / scale + contentCenter

                    val hit = tickets.find {
                        val seat = Offset(it.seatX, it.seatY)
                        (seat - logicalTap).getDistance() <= 20f
                    }

                    hit?.let {
                        onTicketClick(it)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasCenter = Offset(size.width / 2f, size.height / 2f)

            if (!isInitialized) {
                val contentWidth = maxX - minX
                val contentHeight = maxY - minY
                val scaleX = size.width / contentWidth
                val scaleY = size.height / contentHeight
                scale = minOf(scaleX, scaleY) * 0.8f
                isInitialized = true
            }

            withTransform({
                translate(left = canvasCenter.x + offset.x, top = canvasCenter.y + offset.y)
                scale(scale, scale, pivot = Offset.Zero)
                translate(left = -contentCenter.x, top = -contentCenter.y)
            }) {
                sectors.forEach { sector ->
                    val topLeft = Offset(sector.xSx, sector.ySx)
                    val bottomRight = Offset(sector.xDx, sector.yDx)
                    val rect = Rect(topLeft, bottomRight)

                    drawRect(
                        color = if (sector.isStage == 1) MapStage else MapSector,
                        topLeft = topLeft,
                        size = rect.size
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        sector.name,
                        topLeft.x + 8,
                        topLeft.y - 10,
                        Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 28f
                        }
                    )
                }

                tickets.forEach { ticket ->
                    val seatPos = Offset(ticket.seatX, ticket.seatY)

                    drawCircle(
                        color = if (ticket.ticketUserId == null) MapSeat else MapSeatTaken,
                        radius = 10f,
                        center = seatPos
                    )

                    if (checkInCart(ticket.ticketId)) {
                        drawCircle(
                            color = Color.Black,
                            radius = 12f,
                            center = seatPos,
                            style = Stroke(width = 2f)
                        )
                    }
                }
            }
        }
    }
}