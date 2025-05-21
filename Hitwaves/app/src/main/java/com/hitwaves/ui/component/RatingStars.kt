package com.hitwaves.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.R
import com.hitwaves.ui.theme.Secondary

@Composable
fun Rating(){
    var isSelected by remember { mutableIntStateOf(0) }

    Row {
        Text(
            text = "Rating",
            color = Secondary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(Modifier.width(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < isSelected)
                        ImageVector.vectorResource(R.drawable.star_fill)
                    else
                        ImageVector.vectorResource(R.drawable.star_line),
                    contentDescription = "Star",
                    tint = Secondary,
                    modifier = Modifier
                        .size(23.dp)
                        .clickable {
                            isSelected = index + 1
                        }
                )
            }
        }

    }
}

@Composable
fun RatingViewOnly(
    rating: Int,
    starSize: Dp,
    starSpacing: Dp
){

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(starSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating)
                    ImageVector.vectorResource(R.drawable.star_fill)
                else
                    ImageVector.vectorResource(R.drawable.star_line),
                contentDescription = "Star",
                tint = Secondary,
                modifier = Modifier
                    .size(starSize)
            )
        }
    }
}