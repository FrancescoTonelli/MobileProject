package com.hitwaves.ui.component

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.hitwaves.R
import com.hitwaves.api.SectorCanvasResponse
import com.hitwaves.api.TicketCanvasResponse
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.MapBackground
import com.hitwaves.ui.theme.MapSeat
import com.hitwaves.ui.theme.MapSeatTaken
import com.hitwaves.ui.theme.MapSector
import com.hitwaves.ui.theme.MapStage
import com.hitwaves.ui.theme.Secondary

@Composable
fun rememberRubikPaint(): Paint {
    val context = LocalContext.current
    val density = LocalDensity.current
    val paint = remember { Paint().apply { isAntiAlias = true } }

    LaunchedEffect(Unit) {
        paint.apply {
            color = Secondary.toArgb()
            textSize = with(density) { 4.sp.toPx() }
            typeface = ResourcesCompat.getFont(context, R.font.rubik_medium)
        }
    }

    return paint
}

@Composable
fun SeatChart(
    tickets: List<TicketCanvasResponse>,
    sectors: List<SectorCanvasResponse>,
    onTicketClick: (TicketCanvasResponse) -> Unit,
    checkInCart: (Int) -> Boolean
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isInitialized by remember { mutableStateOf(false) }

    val paint = rememberRubikPaint()

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
            .background(MapBackground)
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
                        (seat - logicalTap).getDistance() <= 5f
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
                        topLeft.x + 3,
                        topLeft.y - 8,
                        paint
                    )
                }

                tickets.forEach { ticket ->
                    val seatPos = Offset(ticket.seatX, ticket.seatY)

                    drawCircle(
                        color = if (ticket.ticketUserId == null) MapSeat else MapSeatTaken,
                        radius = 5f,
                        center = seatPos
                    )

                    if (checkInCart(ticket.ticketId)) {
                        drawCircle(
                            color = FgDark,
                            radius = 3f,
                            center = seatPos,
                            style = Stroke(width = 2f)
                        )
                    }
                }
            }
        }
    }
}