package edu.umsl.nasaviewer
import android.content.Context
import android.widget.Toast
import org.json.JSONArray

fun Context.toast(message: CharSequence){           // this just makes for an easier version of toast
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
