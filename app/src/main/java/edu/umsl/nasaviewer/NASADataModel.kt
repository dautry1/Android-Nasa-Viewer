package edu.umsl.nasaviewer

import android.provider.BaseColumns

const val THE_URL_ROOT = "https://images-api.nasa.gov/search?q="    // append the celestial body of interest to this string to retrieve json data
const val NASA_ID_KEY = "nasa_id"           // used when parsing JSON
const val DESCRIPTION_KEY = "description"   // used when parsing JSON
const val HREF_KEY = "href"                 // used when parsing JSON
const val NASA_JPEG_EXTENSION = ".jpg"

object NASAData {               // singleton
    var mTheNASAImageList: ArrayList<NASAImageListItem>? = ArrayList()      // data retrieved from NASA
    val mTheNASATopicList = arrayListOf(        // list of topics in the opening screen
        "Sun",
        "Mercury",
        "Venus",
        "Earth",
        "Mars",
        "Jupiter",
        "Saturn",
        "Uranus",
        "Neptune",
        "Pluto")
}

object DBContract {

    //defines the table contents
    class NASAEntry : BaseColumns {
        companion object {
            const val TABLE_NAME = "nasaaccesslog"
            const val COLUMN_ENTRYNUM = "entrynum"
            const val COLUMN_NASAID = "nasaid"
            const val COLUMN_TIME = "time"
        }
    }
}

object NASADB {
    lateinit var nASAAccessLogDBHelper:NASAAccessLogDBHelper
}
data class NASAImageListItem(val nasaId: String, val description: String, val imageURL: String)
data class NASAAccessLogModel(val entrynum: String, val nasaid: String, val time: String)


