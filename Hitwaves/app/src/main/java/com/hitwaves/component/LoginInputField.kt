package com.hitwaves.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.hitwaves.R
import com.hitwaves.ui.theme.Secondary
import com.hitwaves.ui.theme.Primary
import com.hitwaves.ui.theme.FgDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Secondary) },
        modifier = modifier,
        singleLine = true,
        textStyle = MaterialTheme.typography.labelSmall.copy(color = Secondary),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Secondary,
            unfocusedTextColor = Secondary,
            focusedContainerColor = FgDark,
            unfocusedContainerColor = FgDark,
            focusedBorderColor = Primary,
            unfocusedBorderColor = Secondary,
            cursorColor = Primary
        ),
        shape = MaterialTheme.shapes.extraLarge
    )
}

//@Composable
//fun LoginPasswordField(
//    value: String,
//    onValueChange: (String) -> Unit,
//    label: String,
//    modifier: Modifier = Modifier
//) {
//    var passwordVisible by remember { mutableStateOf(false) }
//
//    LoginInputField(
//        value = value,
//        onValueChange = onValueChange,
//        label = label,
//        isPassword = !passwordVisible,
//        modifier = modifier,
//        trailingIcon = {
//            IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                Icon(
//                    painter = if (passwordVisible)
//                        painterResource(id = R.drawable.ic_eye_open)
//                    else
//                        painterResource(id = R.drawable.ic_eye_closed),
//                    contentDescription = if (passwordVisible) "Nascondi password" else "Mostra password",
//                    tint = MaterialTheme.colorScheme.secondary
//                )
//            }
//        }
//    )
//}
