package com.hitwaves.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hitwaves.ui.theme.*


@Composable
fun Tickets(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()){
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            SecondaryTextTabs()
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SecondaryTextTabs() {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Next Event", "Past Event")
    Column {
        SecondaryTabRow(
            selectedTabIndex = state,
            containerColor = BgDark,
            contentColor = Secondary
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    selectedContentColor = Primary,
                    unselectedContentColor = Secondary,
                    text = {
                        Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                )
            }
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Secondary tab ${state + 1} selected",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}