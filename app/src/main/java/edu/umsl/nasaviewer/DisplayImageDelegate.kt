package edu.umsl.nasaviewer

import android.graphics.drawable.Drawable

interface DisplayImageDelegate {
    fun getImageComplete(result: Long, image: Drawable)  // if result = 1 means it worked
}
