package com.hitwaves.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.R
import com.hitwaves.api.ApiResult
import com.hitwaves.ui.component.AccountReviewCard
import com.hitwaves.ui.component.CustomMessageBox
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.BgDark
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Typography
import com.hitwaves.ui.viewModel.AccountReviewsViewModel

private fun init(): AccountReviewsViewModel {
    return AccountReviewsViewModel()
}

@Composable
fun AccountReviews(navController: NavHostController) {

    val accountRevViewModel = remember { init() }
    val reviews by accountRevViewModel.reviewsState
    val deleteState by accountRevViewModel.deleteState
    val isLoading by accountRevViewModel.isLoadingReviews
    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var idToDelete by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        accountRevViewModel.getReviews()
    }

    LaunchedEffect(reviews) {
       if (reviews.errorMessage != null) {
            snackbarHostState.showSnackbar(reviews.errorMessage!!)
       }
    }

    LaunchedEffect(deleteState) {
        if (deleteState.success) {
            accountRevViewModel.getReviews()
            deleteState.success = false
        } else if (deleteState.errorMessage != null) {
            snackbarHostState.showSnackbar(deleteState.errorMessage!!)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 4.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.notification_open),
                            tint = Secondary,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Your reviews",
                        style = Typography.titleLarge.copy(
                            fontSize = 24.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            if(reviews.data.isNullOrEmpty()) {
                item {
                    Text(
                        text = "No reviews found",
                        style = Typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            color = Secondary
                        ),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(reviews.data ?: emptyList()) { review ->
                    AccountReviewCard(
                        userReviewResponses = review,
                        onDelete = {
                            idToDelete = review.reviewId
                            showDeleteDialog = true
                        },
                    )

                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }

    if (showDeleteDialog) {
        CustomMessageBox(
            title = "Delete review",
            message = "Are you sure you want to delete this review?",
            onConfirm = {
                showDeleteDialog = false
                accountRevViewModel.deleteReview(idToDelete)
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    CustomSnackbar(snackbarHostState)

    if (isLoading) {
        LoadingIndicator()
    }
}