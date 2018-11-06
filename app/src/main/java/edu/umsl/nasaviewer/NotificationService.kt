package edu.umsl.nasaviewer

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.util.*

// This code was ripped and modified from http://devdeeds.com/android-kotlin-create-and-schedule-notification/

class NotificationService : IntentService("NotificationService") {
    private lateinit var mNotification: Notification

    companion object {

        const val CHANNEL_ID = "Channel ID"
        const val NOTIFICATION_ID: Int = 1000
    }

    override fun onHandleIntent(intent: Intent?) {
        var timestamp: Long = 0
        if (intent != null && intent.extras != null) {
            timestamp = intent.extras!!.getLong("timestamp")
        }
        if (timestamp > 0) {
            val context = this.applicationContext
            val yesNotifyIntent = Intent(this, MainActivity::class.java)
            val title = "Come back to NASA"
            val message = "We need a few good astronomers."

            yesNotifyIntent.putExtra("title", title)
            yesNotifyIntent.putExtra("message", message)
            yesNotifyIntent.putExtra("notification", true)

            yesNotifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val yesPendingIntent = PendingIntent.getActivity(context, 0, yesNotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//            val noPendingIntent = PendingIntent.getActivity(context, 0, noNotifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val res = this.resources
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(message).build()
            } else {

                mNotification = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_nasa_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle(title)
                        .setStyle(NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSound(uri)
                        .setContentText(message)
                        .addAction(R.drawable.ic_nasa_logo, "Let's go!", yesPendingIntent)
                        .build()
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            Log.e("notified", "received")
            notificationManager.notify(NOTIFICATION_ID, mNotification)
        }
    }
}