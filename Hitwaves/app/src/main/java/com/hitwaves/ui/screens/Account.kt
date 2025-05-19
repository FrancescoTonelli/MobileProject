package com.hitwaves.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.hitwaves.LoginActivity
import com.hitwaves.R
import com.hitwaves.api.TokenManager
import com.hitwaves.api.getHttpUserImageUrl
import com.hitwaves.ui.component.ButtonWithIcons
import com.hitwaves.ui.component.CustomMessageBox
import com.hitwaves.ui.component.CustomPhotoDialog
import com.hitwaves.ui.component.CustomSnackbar
import com.hitwaves.ui.component.LoadingIndicator
import com.hitwaves.ui.theme.*
import com.hitwaves.ui.viewModel.AccountViewModel
import java.io.ByteArrayOutputStream
import java.util.Locale

private fun init() : AccountViewModel {
    return AccountViewModel()
}

private fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    } catch (e: Exception) {
        null
    }
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
    return stream.toByteArray()
}

@Composable
fun Account(navController: NavHostController) {

    val accountViewModel = remember { init() }
    val accountData by accountViewModel.accountState
    val logoutState by accountViewModel.logoutState
    val deleteState by accountViewModel.deleteState
    val isLoading by accountViewModel.isLoadingAccount
    val imageUpdateState by accountViewModel.imageUpdateState
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var avatar by remember { mutableStateOf("") }

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            val bytes = uriToByteArray(context, it)
            bytes?.let { byteArray ->
                accountViewModel.updateImage(byteArray)
            }
        }
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val bytes = bitmapToByteArray(it)
            accountViewModel.updateImage(bytes)
        }
    }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        accountViewModel.getAccount()
    }

    LaunchedEffect(imageUpdateState) {
        if (imageUpdateState.success) {
            accountViewModel.getAccount()
        } else if (imageUpdateState.errorMessage != null) {
            snackbarHostState.showSnackbar(imageUpdateState.errorMessage!!)
        }
    }

    LaunchedEffect(accountData) {
        if (!accountData.success && accountData.errorMessage != null) {
            snackbarHostState.showSnackbar(accountData.errorMessage!!)
        }
        else {
            avatar = if (accountData.data?.image != null) {
                accountData.data?.image + "?ts=${System.currentTimeMillis()}"
            } else {
                "default.png?ts=${System.currentTimeMillis()}"
            }
        }
    }

    LaunchedEffect(logoutState) {
        if (logoutState.success) {
            TokenManager.clearToken()
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()

        } else if (logoutState.errorMessage != null) {
            snackbarHostState.showSnackbar(logoutState.errorMessage!!)
        }
    }

    LaunchedEffect(deleteState) {
        if (deleteState.success) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()

        } else if (deleteState.errorMessage != null) {
            snackbarHostState.showSnackbar(deleteState.errorMessage!!)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable { showImagePickerDialog = true },
                    contentAlignment = Alignment.Center
                ) {

                    Image(

                        painter = rememberAsyncImagePainter(
                            model = getHttpUserImageUrl(avatar)
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BgDark.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.edit),
                            contentDescription = null,
                            tint = Secondary,
                            modifier = Modifier
                                .size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))


                Text(
                    text = accountData.data?.username ?: "",
                    style = Typography.titleLarge.copy(
                        fontSize = 22.sp,
                        color = Secondary
                    )
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Secondary
            )

            Column (
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Refunds",
                        style = Typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "€ ${ String.format(Locale.US, "%.2f", accountData.data?.refunds) }",
                        style = Typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            color = Secondary
                        )
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Secondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable {
                            navController.navigate("account_update")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Your Data",
                        style = Typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow),
                            contentDescription = "Details",
                            tint = FgDark,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                                .size(20.dp)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Secondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable {
                            navController.navigate("account_reviews")
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Your Reviews",
                        style = Typography.bodyLarge.copy(
                            fontSize = 18.sp,
                            color = Secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.arrow),
                            contentDescription = "Details",
                            tint = FgDark,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                                .size(20.dp)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp),
                    color = Secondary
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ButtonWithIcons(
                        startIcon = ImageVector.vectorResource(R.drawable.logout),
                        textBtn = "Logout",
                        endIcon = ImageVector.vectorResource(R.drawable.arrow),
                        onClickAction = {
                            showLogoutDialog = true
                        }
                    )

                    ButtonWithIcons(
                        startIcon = ImageVector.vectorResource(R.drawable.delete),
                        textBtn = "Delete",
                        endIcon = ImageVector.vectorResource(R.drawable.arrow),
                        onClickAction = {
                            showDeleteDialog = true
                        }
                    )
                }

            }

        }
    }


    if (showLogoutDialog) {
        CustomMessageBox(
            title = "Logout",
            message = "Are you sure you want to logout?",
            onConfirm = {
                showLogoutDialog = false
                accountViewModel.logout()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        CustomMessageBox(
            title = "Delete",
            message = "Are you sure you want to delete your account?",
            onConfirm = {
                showDeleteDialog = false
                accountViewModel.deleteAccount()
            },
            onDismiss = {
                showDeleteDialog = false
            }
        )
    }

    if (showImagePickerDialog) {
        CustomPhotoDialog(
            title = "Change Profile Picture",
            message = "Choose an image source:",
            onConfirmGallery = {
                showImagePickerDialog = false
                pickImageLauncher.launch("image/*")
            },
            onDismiss = {
                showImagePickerDialog = false
            },
            onConfirmCamera = {
                showImagePickerDialog = false
                takePhotoLauncher.launch(null)
            }
        )
    }


    CustomSnackbar(snackbarHostState)

    if (isLoading) {
        LoadingIndicator()
    }
}