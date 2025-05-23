package com.hitwaves.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.api.getHttpConcertImageUrl
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.model.SectorConcert
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.ShowArtistList
import com.hitwaves.ui.component.Title
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.theme.rememberScreenDimensions
import com.hitwaves.ui.viewModel.ConcertViewModel
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import com.hitwaves.R
import com.hitwaves.api.ConcertInfoResponse
import com.hitwaves.api.TicketConcertDetailsResponse
import com.hitwaves.api.getHttpTourImageUrl
import com.hitwaves.ui.component.ButtonWithIcons
import com.hitwaves.ui.component.DetailRow
import com.hitwaves.ui.component.GmapsDetailRow
import java.text.NumberFormat
import java.util.Locale


private fun init(): ConcertViewModel {
    return ConcertViewModel()
}

@Composable
fun ConcertDetails(eventForCards: EventForCards, navController: NavController) {

    val concertViewModel = remember { init() }
    val concert by concertViewModel.concertState
    val isLoading by concertViewModel.isLoadingConcert

    val snackBarHostState = remember { SnackbarHostState() }

    var concertArtist : List<Artist> by remember { mutableStateOf(emptyList()) }
    var concertInfo: ConcertInfoResponse? by remember { mutableStateOf(null) }
    var concertSector: List<SectorConcert> by remember { mutableStateOf(emptyList()) }
    var concertTickets: Map<Int, List<TicketConcertDetailsResponse>> by remember { mutableStateOf(emptyMap()) }

    var quantity by remember { mutableIntStateOf(1) }
    var selectedSector by remember { mutableStateOf<SectorConcert?>(null) }


    LaunchedEffect(Unit) {
        concertViewModel.getConcertInfo(eventForCards.contentId)
    }

    LaunchedEffect(concert) {
        if (concert.success && concert.data != null) {
            val info = concert.data!!.concertInfo

            concertInfo = info

            concertArtist = concert.data!!.artists.map { artist ->
                Artist(
                    artistId = artist.artistId,
                    artistName = artist.artistName,
                    artistImageUrl = artist.artistImage,
                    likesCount = null,
                    averageRating = null,
                    isLiked = false
                )
            }

            concertSector = concert.data!!.sectors
                .filter { it.isStage == 0 }
                .map { sector ->
                SectorConcert(
                    id = sector.id,
                    name = sector.name,
                    isStage = sector.isStage == 1
                )
            }

            concertTickets = concert.data!!.availableTickets.groupBy { it.sectorId }


        } else if (!concert.success && concert.errorMessage != null) {
            snackBarHostState.showSnackbar(concert.errorMessage!!)
        }
    }


    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Box(
            modifier = Modifier
                .size(rememberScreenDimensions().screenWidth, 150.dp)
        ){

            Image(
                painter = rememberAsyncImagePainter(
                    if (concert.success && concert.data != null && concertInfo != null) {
                        if (concertInfo!!.isPartOfTour) {
                            getHttpTourImageUrl(concertInfo!!.effectiveImage)
                        }else {
                            getHttpConcertImageUrl(concertInfo!!.effectiveImage)
                        }
                    }
                    else {
                        ""
                    }
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom

            ) {
                Box (
                    modifier = Modifier
                        .background(Primary)
                ){
                    Text(
                        text = if(concert.success && concert.data != null && concertInfo != null) {
                            if(concertInfo!!.isPartOfTour) {
                                "${concertInfo!!.tourTitle} - ${concertInfo!!.concertTitle}"
                            }else{
                                concertInfo!!.concertTitle
                            }
                        }else {
                            ""
                        },
                        style = Typography.bodyLarge.copy(
                            color = Secondary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Box (
                    modifier = Modifier
                        .background(FgDark)
                ){
                    Text(
                        text = "Concert",
                        style = Typography.bodyLarge.copy(
                            color = Secondary,
                            fontSize = 12.sp,
                            fontStyle = FontStyle.Normal
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
            GoBack(navController)
        }

        LazyColumn (
            modifier = Modifier
                .width(rememberScreenDimensions().screenWidth * 0.9f)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            item {
                Title("Artists")
            }

            item {
                ShowArtistList(concertArtist, navController)
            }

            item {
                Title("Details")
            }

            item {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (concert.success && concert.data!=null && concertInfo != null) {
                        DetailRow("Place", concertInfo!!.placeName, displayDivider = false)
                        GmapsDetailRow("Address", concertInfo!!.placeAddress, displayDivider = false)
                        DetailRow("Date", concertInfo!!.concertDate, displayDivider = false)
                        DetailRow("Time", concertInfo!!.concertTime, displayDivider = false)
                    }
                }
            }

            item {
                Title("Choose your ticket")
            }

            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //Quantity
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Quantity",
                            style = Typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(end = 16.dp)
                        )


                        QuantitySelector(
                            quantity = quantity,
                            onQuantityChange = { quantity = it }
                        )
                    }

                    //Sector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Sector",
                            style = Typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(end = 16.dp)
                        )

                        SimpleDropdownMenu(
                            options = concertSector,
                            selectedOption = selectedSector,
                            onOptionSelected = { selectedSector = it }
                        )
                    }

                    val seatDescription = concertTickets[selectedSector?.id]?.firstOrNull()?.seatDescription ?: "Select a sector"



                    val ticketPrice = if (concertTickets[selectedSector?.id] != null) {
                        "€ ${ String.format(Locale.US, "%.2f", concertTickets[selectedSector?.id]?.firstOrNull()?.ticketPrice) }"
                    }
                    else {
                        "Select a sector"
                    }

                    DetailRow("Seat", seatDescription, displayDivider = false)
                    DetailRow("Price", ticketPrice, displayDivider = false)

                    ButtonWithIcons(ImageVector.vectorResource(R.drawable.seat), "Seating chart", ImageVector.vectorResource(R.drawable.arrow)) { }
                }

            }

            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ){
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(width = 184.dp, height = 152.dp),
                        tint = Primary.copy(alpha = 0.8f)
                    )

                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(25.dp)
                    ) {
                        Title("Ready for the wave?")

                        ButtonWithIcons(ImageVector.vectorResource(R.drawable.card), "Checkout", ImageVector.vectorResource(R.drawable.arrow)) { }
                    }
                }
            }

        }
    }


    CustomSnackBar(snackBarHostState)


    if (isLoading) {
        LoadingIndicator()
    }
}



@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Icon(
            ImageVector.vectorResource(R.drawable.remove_btn),
            contentDescription = "Decrease",
            tint = Secondary,
            modifier = Modifier
                .padding(end = 16.dp)
                .clickable { onQuantityChange(quantity - 1) }
        )

        BasicTextField(
            value = quantity.toString(),
            onValueChange = {},
            readOnly = true,
            textStyle = Typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                color = Secondary,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier
                .width(50.dp)
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)
                .border(1.dp, Secondary.copy(alpha = 0.7f), RectangleShape)
                .padding(vertical = 2.dp),
        )

        Icon(
            ImageVector.vectorResource(R.drawable.add_btn),
            contentDescription = "Increase",
            tint = Secondary,
            modifier = Modifier
                .padding(start = 16.dp)
                .clickable { onQuantityChange(quantity + 1) }
        )

    }
}


@Composable
fun SimpleDropdownMenu(
    options: List<SectorConcert>,
    selectedOption: SectorConcert?,
    onOptionSelected: (SectorConcert) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .background(BgDark),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedOption?.name ?: "Select sector",
                style = Typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = Secondary,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow),
                contentDescription = "Arrow",
                tint = Secondary,
                modifier = Modifier.rotate(90f)
            )
        }


        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(BgDark)
        ) {
            options.forEach { option ->
                Text(
                    text = option.name,
                    style = Typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        color = Secondary,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onOptionSelected(option)
                            expanded = false
                        }
                        .background(Color.Transparent)
                        .padding(8.dp),
                )
            }
        }
    }
}


