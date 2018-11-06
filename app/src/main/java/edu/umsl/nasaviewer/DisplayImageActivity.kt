package edu.umsl.nasaviewer

import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_display_image.*
import android.os.AsyncTask
import android.text.method.ScrollingMovementMethod
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import android.view.MenuItem


class DisplayImageActivity : AppCompatActivity(), DisplayImageDelegate  {

    private var mGetImageTask = GetImage("", this)

    companion object {
        val context = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)

        val position = intent.extras.getInt("position")
        descriptionTV.text = NASAData.mTheNASAImageList!![position].description
        descriptionTV.movementMethod = ScrollingMovementMethod()        // for large descriptions, it needs to be scrollable
        val imageURL = NASAData.mTheNASAImageList!![position].imageURL  // position is what ever the user click on
        mGetImageTask = GetImage(imageURL, this)            // the delegate is the listerner for when the async task is finished
        mGetImageTask.execute().get()
    }

    override fun getImageComplete(result: Long, image: Drawable) {
        if (result == 1.toLong()) {
            nasaImageIV.setImageDrawable(image)             // show the NASA jpeg in the drawable
        } else {
            toast("The image is not retrievable.")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {       // this one handles the back arrow in the upper left hand-side of the screen
        if (item.itemId == android.R.id.home) {
            onBackPressed()     // this will prevent a crash if the back button is rapidly pressed twice in a row
            return true
        }
        return false
    }
}

class GetImage(theURL: String, delegate: DisplayImageDelegate) : AsyncTask<String, String, Long>() {
        private var theDrawableImage: Drawable? = null
        private val mTheURL = theURL
        private val theDelegate = delegate


        override fun onPostExecute(result: Long) {  // when the async network jpeg download is complete
                                                    // let the delegate know so it can show the image
                theDelegate.getImageComplete(result, theDrawableImage!!)
                super.onPostExecute(result)
        }

        override fun doInBackground(vararg params: String?): Long {
            var theReturnCode: Long

            try {
                val inputStream:InputStream = fetch(mTheURL) as InputStream
                theDrawableImage = Drawable.createFromStream(inputStream, "src")
                theReturnCode = 1
            } catch (e: IOException) {
                theDrawableImage = null
                theReturnCode = 0
            }
            return theReturnCode
        }

        @Throws(MalformedURLException::class, IOException::class)
        private fun fetch(address: String): Any {
            val url = URL(address)
            return url.content
        }
    }
