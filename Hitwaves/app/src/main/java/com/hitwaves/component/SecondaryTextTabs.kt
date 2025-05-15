package com.hitwaves.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.*

@Composable
fun SecondaryTextTabs() {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Next Event", "Past Event")
    Column {
        SecondaryTabRow(
            selectedTabIndex = state,
            containerColor = BgDark,
            contentColor = Secondary,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(state, matchContentSize = false),
                    color = Primary
                )
            }
        ) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    selectedContentColor = Primary,
                    unselectedContentColor = Secondary,
                    text = {
                        Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis
                            , fontFamily = Rubik, fontSize = 20.sp)
                    }
                )
            }
        }
//        Text(
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            text = "Secondary tab ${state + 1} selected",
//            style = MaterialTheme.typography.bodyLarge
//        )
    }
}