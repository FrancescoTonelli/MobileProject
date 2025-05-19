package com.hitwaves.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.*
import com.hitwaves.R

@Composable
fun NotificationCard(
    title: String,
    isRead: Boolean,
    onClick: () -> Unit
) {
    val readColor = if (isRead) Secondary else Primary
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    ImageVector.vectorResource(if(isRead) R.drawable.notification_open else R.drawable.notification_closed),
                    tint = readColor,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = Typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = readColor,
                    fontWeight = if(isRead) FontWeight.Normal else FontWeight.Bold
                )
            )
        }


        HorizontalDivider(
            thickness = 1.dp,
            color = readColor
        )
    }
}