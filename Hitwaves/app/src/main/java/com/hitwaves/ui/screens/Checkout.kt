package com.hitwaves.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.R
import com.hitwaves.api.TicketPurchaseRequest
import com.hitwaves.api.getHttpConcertImageUrl
import com.hitwaves.api.getHttpTourImageUrl
import com.hitwaves.model.DataToCheckout
import com.hitwaves.ui.component.ButtonWithIcons
import com.hitwaves.ui.component.CustomMessageBox
import com.hitwaves.ui.component.CustomSnackBar
import com.hitwaves.ui.component.ExpirationDateInputField
import com.hitwaves.ui.component.GoBack
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.component.NumberInputField
import com.hitwaves.ui.theme.FgDark
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography
import com.hitwaves.ui.viewModel.CheckoutViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.abs

private fun init(): CheckoutViewModel {
    return CheckoutViewModel()
}

@Composable
fun Checkout(navController: NavController, checkoutData: DataToCheckout, innerPadding: PaddingValues) {
    val cardNumber = remember { mutableStateOf("") }
    val expDate = remember { mutableStateOf("") }
    val cvv = remember { mutableStateOf("") }

    val checkoutViewModel = remember { init() }
    val checkoutDisplay by checkoutViewModel.checkoutDisplay
    val isLoading by checkoutViewModel.isLoading
    val purchaseState by checkoutViewModel.purchaseState
    var showBuyDialog by remember { mutableStateOf(false) }

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        checkoutViewModel.getCheckoutDisplay(checkoutData.concertId)
    }

    LaunchedEffect(checkoutDisplay) {
        if (!checkoutDisplay.success && !isLoading) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(checkoutDisplay.errorMessage ?: "An error occurred while checking review existence.")
            }
        }
    }

    LaunchedEffect(purchaseState) {
        if (purchaseState.success && !isLoading) {
            navController.navigate("tickets") {
                popUpTo("checkout") {
                    inclusive = true
                }

            }
        } else if (!purchaseState.success && !isLoading) {
            coroutineScope.launch {
                snackBarHostState.showSnackbar(purchaseState.errorMessage ?: "An error occurred while purchasing tickets.")
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(innerPadding)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = rememberAsyncImagePainter(
                        if (checkoutDisplay.success && checkoutDisplay.data != null) {
                            if (checkoutDisplay.data!!.tourTitle.isNullOrEmpty()) {
                                getHttpConcertImageUrl(checkoutDisplay.data!!.image)
                            } else {
                                getHttpTourImageUrl(checkoutDisplay.data!!.image)
                            }
                        } else {
                            getHttpConcertImageUrl(null)
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
                        (if (checkoutDisplay.success && checkoutDisplay.data != null) {
                            if (checkoutDisplay.data?.tourTitle.isNullOrEmpty()) {
                                checkoutDisplay.data?.title
                            } else {
                                "${checkoutDisplay.data?.tourTitle} - ${checkoutDisplay.data?.title}"
                            }
                        } else {
                            checkoutDisplay.errorMessage.toString()
                        })?.let {
                            Text(
                                text = it,
                                style = Typography.titleLarge.copy(
                                    fontSize = 18.sp,
                                    color = Secondary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Box (
                        modifier = Modifier
                            .background(FgDark)
                    ){
                        Text(
                            text = "Concert",
                            style = Typography.titleLarge.copy(
                                fontSize = 12.sp,
                                color = Secondary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                for (ticket in checkoutData.tickets) {
                    CheckoutRow(
                        title = "${ticket.sectorName}, ${ticket.seatDescription}",
                        value = ticket.price
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    thickness = 1.dp,
                    color = Secondary
                )

                val subtotal = checkoutData.tickets.sumOf { it.price }
                CheckoutRow(
                    title = "Subtotal",
                    value = subtotal
                )

                CheckoutRow(
                    title = "Refunds",
                    value = if (subtotal < (checkoutDisplay.data?.refunds ?: 0.0))
                        -subtotal
                    else
                            (checkoutDisplay.data?.refunds ?: 0.0) * -1.0
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    thickness = 1.dp,
                    color = Secondary
                )

                CheckoutRow(
                    title = "Total",
                    value = subtotal - (checkoutDisplay.data?.refunds ?: 0.0)
                )

                Spacer(modifier = Modifier.height(40.dp))

                NumberInputField(
                    value = cardNumber.value,
                    onValueChange = {
                        cardNumber.value = it
                    },
                    label = "Card Number",
                    isCardNumber = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ExpirationDateInputField(
                        value = expDate.value,
                        onValueChange = { expDate.value = it },
                        label = "Exp. Date",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    NumberInputField(
                        value = cvv.value,
                        onValueChange = { cvv.value = it },
                        label = "CVV",
                        maxDigits = 3,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                ButtonWithIcons(
                    ImageVector.vectorResource(R.drawable.card),
                    "Confirm",
                    ImageVector.vectorResource(R.drawable.arrow),
                    onClickAction = {
                        showBuyDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(40.dp))

            }
        }
    }

    if (showBuyDialog) {
        CustomMessageBox(
            title = "Buy tickets",
            message = "Are you sure you want to complete the purchase? " +
                    "You will not be able to cancel it later.",
            onConfirm = {
                showBuyDialog = false
                checkoutViewModel.buyTickets(
                    TicketPurchaseRequest(
                        ticketIds = checkoutData.tickets.map { it.ticketId }
                    )
                )
            },
            onDismiss = {
                showBuyDialog = false
            }
        )
    }

    GoBack(navController)

    if(isLoading) {
        LoadingIndicator()
    }

    CustomSnackBar(snackBarHostState)
}

@Composable
fun CheckoutRow(
    title: String,
    value: Double
) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = Typography.titleLarge.copy(
                    fontSize = 16.sp,
                    color = Secondary,
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.Start
            )

            Text(
                text = if (value < 0.0) {
                    "- € ${ String.format(Locale.US, "%.2f", abs(value)) }"
                } else {
                    "€ ${ String.format(Locale.US, "%.2f", value) }"
                },
                style = Typography.titleLarge.copy(
                    fontSize = 16.sp,
                    color = Secondary,
                    fontWeight = FontWeight.Normal
                ),
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}