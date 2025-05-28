package com.hitwaves.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hitwaves.AppActivity
import com.hitwaves.R
import com.hitwaves.api.TokenManager
import com.hitwaves.api.apiSendFcmToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val channelID = "notification_channel"
const val channelName = "com.hitwaves.utils.notification"

class NotificationService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {

        Log.d("FCM", "New token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            while(TokenManager.getToken() == null) {
                delay(100)
            }
            val result = apiSendFcmToken(token)
            withContext(Dispatchers.Main) {
                if(!result.success){
                    Log.e("FCM", "Token registration error: ${result.errorMessage}")
                }
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification != null){
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
            CoroutineScope(Dispatchers.IO).launch {
                UnreadBadge.update()
            }
        }
    }

    private fun generateNotification(title: String, text: String) {
        val intent = Intent(this, AppActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)


        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.logo)
            .setColor(ContextCompat.getColor(this, R.color.primary))
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, builder.build())
    }
}