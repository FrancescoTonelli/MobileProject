package com.hitwaves.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.screens.Account
import com.hitwaves.ui.screens.AccountReviews
import com.hitwaves.ui.screens.AccountUpdate
import com.hitwaves.ui.screens.ArtistDetails
import com.hitwaves.ui.screens.ConcertDetails
import com.hitwaves.ui.screens.ConcertMap
import com.hitwaves.ui.screens.TourDetails
import com.hitwaves.ui.screens.Home
import com.hitwaves.ui.screens.Likes
import com.hitwaves.ui.screens.Login
import com.hitwaves.ui.screens.Notification
import com.hitwaves.ui.screens.NotificationDetails
import com.hitwaves.ui.screens.Tickets

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("login") {
            Login(navController)
        }
        composable("home") {
            Home(navController)
        }
        composable("likes") {
            Likes(navController)
        }
        composable("tickets") {
            Tickets(navController)
        }
        composable("account") {
            Account(navController)
        }
        composable("account_update") {
            AccountUpdate(navController)
        }
        composable("account_reviews") {
            AccountReviews(navController)
        }
        composable("notifications") {
            Notification(navController)
        }
        composable(
            route = "notificationDetails/{title}/{description}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val description = backStackEntry.arguments?.getString("description") ?: ""
            NotificationDetails(navController, title, description)
        }

        composable("map") {
            ConcertMap(navController)
        }
        composable("tourDetails") {
            val eventForCards = remember {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<EventForCards>("event")
            }

            if (eventForCards != null) {
                TourDetails(eventForCards = eventForCards, navController = navController)
            } else {
                Text("Event not available")
            }
        }

        composable("artistDetails") {
            val artist = remember {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<Artist>("artist")
            }
            if (artist != null) {
                ArtistDetails(artist = artist, navController = navController)
            } else {
                Text("Artist not available")
            }
        }

        composable("concertDetails") {
            val eventForCards = remember {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<EventForCards>("event")
            }

            if (eventForCards != null) {
                ConcertDetails(eventForCards = eventForCards, navController = navController)
            } else {
                Text("Event not available")
            }
        }

    }
}