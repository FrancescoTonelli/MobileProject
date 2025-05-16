package com.hitwaves.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hitwaves.model.Artist
import com.hitwaves.model.EventForCards
import com.hitwaves.ui.screens.Account
import com.hitwaves.ui.screens.ArtistDetails
import com.hitwaves.ui.screens.ConcertMap
import com.hitwaves.ui.screens.EventDetails
import com.hitwaves.ui.screens.Home
import com.hitwaves.ui.screens.Likes
import com.hitwaves.ui.screens.Login
import com.hitwaves.ui.screens.Notification
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
        composable("notifications") {
            Notification(navController)
        }
        composable("map") {
            ConcertMap(navController)
        }
        composable("eventDetails") {
            val eventForCards = remember {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<EventForCards>("event")
            }

            if (eventForCards != null) {
                EventDetails(eventForCards = eventForCards, navController = navController)
            } else {
                Text("Evento non disponibile")
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
                Text("Artista non disponibile")
            }
        }
    }
}