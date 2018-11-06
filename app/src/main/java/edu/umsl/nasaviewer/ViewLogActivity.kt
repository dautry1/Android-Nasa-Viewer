package edu.umsl.nasaviewer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_view_log.*

class ViewLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_view_log)

        var logString = ""
        for (item in NASADB.nASAAccessLogDBHelper.readAllNASALog()) {
            logString = logString.plus(item.nasaid).plus(" ").plus(item.time).plus("\n")
        }
        logTV.text = logString
        logTV.movementMethod = ScrollingMovementMethod()
    }
}
