package com.hitwaves.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hitwaves.ui.theme.*

@Composable
fun ButtonWithIcons(
    startIcon: ImageVector? = null,
    textBtn: String,
    endIcon: ImageVector? = null,
    onClickAction: () -> Unit
) {
    Button(
        onClick = onClickAction,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Primary,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            startIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = FgDark,
                )
            }

            Text(
                text = textBtn,
                style = Typography.bodyLarge.copy(
                    color = FgDark,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            endIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = FgDark
                )
            }
        }
    }
}

@Composable
fun LoginButton(
    textBtn: String,
    onClickAction: () -> Unit
) {
    Button(
        onClick = onClickAction,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Primary,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        modifier = Modifier.padding(vertical = 8.dp)
            .height(46.dp)
            .width(144.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = textBtn,
            style = Typography.bodyLarge.copy(
                color = FgDark,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun SecondaryLoginButton(
    textBtn: String,
    onClickAction: () -> Unit
) {
    Button(
        onClick = onClickAction,
        colors = ButtonDefaults.buttonColors(
            containerColor = FgDark,
            contentColor = Primary,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,

        ),
        border = BorderStroke(
            width = 1.dp,
            color = Primary
        ),
        modifier = Modifier.padding(vertical = 8.dp)
            .height(44.dp)
            .width(140.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = textBtn,
            style = Typography.bodyLarge.copy(
                color = Primary,
                fontWeight = FontWeight.Bold
            )
        )
    }
}