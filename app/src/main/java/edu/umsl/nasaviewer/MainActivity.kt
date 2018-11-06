//***********************************************************
//***                                                     ***
//***                   NASA Viewer                       ***
//***             Created by David Autry                  ***
//***    for COMPSCI 5020 Final Project Requirement       ***
//***                                                     ***
//***    In this app you will find:                       ***
//***                                                     ***
//***    Activities at least x 3                          ***
//***    Fragments x 2                                    ***
//***    AsyncTask x 2                                    ***
//***    Network Request x 2                              ***
//***    Delegate Interface x 2                           ***
//***    Recycler View/Holder/Adapter x 2                 ***
//***    Animations x 2 - one done programmatically       ***
//***                     & one by xml                    ***
//***    Notifcation x 1                                  ***
//***    SQLite usage - 1 table
//***                                                     ***
//***                                                     ***
//***                                                     ***

//***                                                     ***
//***********************************************************



package edu.umsl.nasaviewer


import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val NOTIFICATION_DURATION = 1000          // in milliseconds

class MainActivity : AppCompatActivity(), TopicListFragment.OnTopicListFragmentInteractionListener {

    private val mTopicListFragment = TopicListFragment.newInstance(1)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // no landscape orientation allowed
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.topicListContainer, mTopicListFragment).commit()
        mTopicListFragment.mListAdapter?.notifyDataSetChanged()
        viewLogButton.setOnClickListener {          // take user to view the access log
            val viewLogIntent = Intent(this, ViewLogActivity::class.java)
            startActivity(viewLogIntent)
        }

        val logoFadeAnimator: Animation = AnimationUtils.loadAnimation(this, R.anim.nasa_logo_alpha_animator)
        nasa_logo.startAnimation(logoFadeAnimator)      // fade the NASA logo in and out
        NASADB.nASAAccessLogDBHelper = NASAAccessLogDBHelper(this)  // DB will keep a log of nasa access
    }

    override fun onTopicListFragmentInteraction(entryPosition: Int) {
        val showImageListIntent = Intent(this, ShowImageListActivity::class.java)
        showImageListIntent.putExtra("position", entryPosition)
        startActivity(showImageListIntent)
    }

    override fun onResume() {
        NotificationUtils.cancelNotification(NotificationUtils.mNotificationTime, this@MainActivity)
        NotificationManagerCompat.from(this).cancel(NotificationService.NOTIFICATION_ID)
        super.onResume()
    }
    override fun onDestroy() {
        val mNotificationTime = Calendar.getInstance().timeInMillis + NOTIFICATION_DURATION
        NotificationUtils.setNotification(mNotificationTime, this@MainActivity)
        super.onDestroy()
    }
}

