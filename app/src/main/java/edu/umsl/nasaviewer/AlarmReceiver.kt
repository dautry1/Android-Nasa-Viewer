package edu.umsl.nasaviewer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

// This code was ripped and modified from http://devdeeds.com/android-kotlin-create-and-schedule-notification/

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val service = Intent(context, NotificationService::class.java)
        service.putExtra("reason", intent.getStringExtra("reason"))
        service.putExtra("timestamp", intent.getLongExtra("timestamp", 0))

            context.startService(service)
    }

}