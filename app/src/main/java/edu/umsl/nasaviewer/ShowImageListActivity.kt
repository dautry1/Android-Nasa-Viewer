package edu.umsl.nasaviewer

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_show_image_list.*
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import org.json.*
import java.util.*

const val CHOOSE_AN_IMAGE = "Pick an image to view"
class ShowImageListActivity : AppCompatActivity(), ShowImageListDelagate, ImageListFragment.OnImageListFragmentInteractionListener  {

    private var mImageListFragment = ImageListFragment.newInstance(1)
    private var position: Int = 0
    private var animateShuttleHandler = Handler()
    private lateinit var animateShuttleRunnable: Runnable       // this will move the shuttle from left to right

    override fun onCreate(savedInstanceState: Bundle?) {

        animateShuttleRunnable = Runnable {
            showImageListAnimationIV.x = 0f
            showImageListAnimationIV.animate().translationX(300f).setDuration(10000).start()
            animateShuttleHandler.postDelayed(animateShuttleRunnable, 10000)
        }

        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_show_image_list)
        val extras = intent.extras
        if (extras != null) {
            if (extras.containsKey("position")) {
                position = extras.getInt("position")
                val chooseString = CHOOSE_AN_IMAGE + NASAData.mTheNASATopicList[position]
                pickImageTV.text = chooseString
            }
        } else {
            position = -1           // -1 indicates that the activity is returning from the view image activity
            // and therefore the image list does not need to be repopulated
        }

        animateShuttleHandler.post(animateShuttleRunnable)
        supportFragmentManager.beginTransaction().add(R.id.imageListContainer, mImageListFragment).commit()
        if (position > -1) {
            if (isOnline()) {
                val theURL = THE_URL_ROOT + NASAData.mTheNASATopicList[position]
                val task = GetData(theURL, this)
                task.execute()
            } else {
                toast("Internet access is not available.")
            }
        } else {
            mImageListFragment.mImageListAdapter?.notifyDataSetChanged()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("position", position)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        position = savedInstanceState!!.getInt("position")
        super.onRestoreInstanceState(savedInstanceState)
    }
//    override fun onPause() {
//
//        animateShuttleHandler.removeCallbacksAndMessages(animateShuttleHandler)
//        super.onPause()
//    }

    override fun onStop() {

        animateShuttleHandler.removeCallbacksAndMessages(animateShuttleHandler)
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun getImageListComplete(result: Long, data: String) {
        var theNASADataJSONObject: JSONObject
        var theNASADataEntry: JSONObject
        var theNASAHrefEntry: JSONObject
        var theNASASubDataDataJSON: JSONArray
        var theNASASubDataLinks: JSONArray
        var href: String?
        val theDataJSONCollection: JSONObject? = JSONObject(data)
        val theNASADataJSONItemList: JSONObject? = JSONObject(theDataJSONCollection!!.optString("collection"))
        val theNASADataEachJSONItemArray = theNASADataJSONItemList!!.optJSONArray("items") as JSONArray
        NASAData.mTheNASAImageList!!.clear()

        // extract data from JSON - there is probably an easier way to do this
        if (result == 1.toLong()) {
            for (i in 0..(theNASADataEachJSONItemArray.length() - 1)) {
                theNASADataJSONObject = theNASADataEachJSONItemArray.get(i) as JSONObject
                theNASASubDataDataJSON = theNASADataJSONObject!!.get("data") as JSONArray
                theNASASubDataLinks = theNASADataJSONObject!!.get("links") as JSONArray
                theNASADataEntry = theNASASubDataDataJSON.get(0) as JSONObject
                theNASAHrefEntry = theNASASubDataLinks.get(0) as JSONObject
                href = theNASAHrefEntry.getString("href")
                if (href.contains(NASA_JPEG_EXTENSION)) {        // since some hrefs may be videos, therefore exclude those and only allow jpg
                    NASAData.mTheNASAImageList!!.add(NASAImageListItem(theNASADataEntry.optString(NASA_ID_KEY), theNASADataEntry.optString(DESCRIPTION_KEY), theNASAHrefEntry.optString(HREF_KEY)))
                }
            }
        }
        else {
            toast("Something went wrong with the network access.")
        }
        mImageListFragment.mImageListAdapter?.notifyDataSetChanged()
    }

    override fun onImageListFragmentInteraction(position: Int) {
        // log the NASA image access to the database
        val result = NASADB.nASAAccessLogDBHelper.insertNASALog(NASAAccessLogModel(0.toString(), NASAData.mTheNASAImageList!![position].nasaId, Calendar.getInstance().time.toString()))
        val displayImageIntent = Intent(this, DisplayImageActivity::class.java)
        displayImageIntent.putExtra("position", position)
        startActivity(displayImageIntent)       // go to the image viewer
    }

// the networking code below was ripped and modified from pages 291 - 294 of
// "Sams Teach Yourself Android Application Development" by Delessio, Darcey, & Conder 4th edition

    private fun isOnline(): Boolean {
        val returnValue: Boolean
        val networkInfo: NetworkInfo
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
        if (connectivityManager is ConnectivityManager) {
            networkInfo = connectivityManager.activeNetworkInfo
            returnValue = networkInfo.isConnected
        } else {
            returnValue = false
        }
        return returnValue
    }

    private class GetData(theURL: String, delegate: ShowImageListDelagate) : AsyncTask<String, String, Long>() {
        private val theDelegate = delegate
        private val mTheURL = theURL
        private var dataString: String? = null

        override fun onPostExecute(result: Long) {
                theDelegate.getImageListComplete(result, dataString!!)
                super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): Long {
            var connection: HttpURLConnection? = null
            var returnCode: Long
            try {
                val dataURL = URL(mTheURL)
                connection = dataURL.openConnection() as HttpURLConnection
                connection.connect()
                val status: Int = connection.responseCode
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var responseString = reader.readLine()
                if (status == 200) {

                    while (responseString != null) {
                        stringBuilder.append(responseString)
                        responseString = reader.readLine()
                    }
                    dataString = stringBuilder.toString()
                    returnCode = 1
                } else {
                    returnCode = 11
                }
                return returnCode
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                return 11
            } catch (e: IOException) {
                e.printStackTrace()
                return 11
            } catch (e: NullPointerException) {
                e.printStackTrace()
                return 11
            } catch (e: JSONException) {
                e.printStackTrace()
                return 11
            } finally {
                connection!!.disconnect()
            }
        }
    }
}