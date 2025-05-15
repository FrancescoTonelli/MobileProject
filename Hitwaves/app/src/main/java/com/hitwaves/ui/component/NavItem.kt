package com.hitwaves.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.hitwaves.R

@Composable
fun getBottomNavItems(): List<IconData> {
    return listOf(
        IconData(
            route = "home",
            label = "Home",
            icon = ImageVector.vectorResource(id = R.drawable.home_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.home_fill)
        ),
        IconData(
            route = "likes",
            label = "Likes",
            icon = ImageVector.vectorResource(id = R.drawable.like_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.like_fill)
        ),
        IconData(
            route = "tickets",
            label = "Tickets",
            icon = ImageVector.vectorResource(id = R.drawable.ticket_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.ticket_fill)
        ),
        IconData(
            route = "account",
            label = "Account",
            icon = ImageVector.vectorResource(id = R.drawable.account_line),
            selectedIcon = ImageVector.vectorResource(id = R.drawable.account_fill)
        )

    )
}