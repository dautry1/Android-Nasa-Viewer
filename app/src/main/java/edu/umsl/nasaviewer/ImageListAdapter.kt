package edu.umsl.nasaviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.image_entry.view.*
import kotlinx.android.synthetic.main.topic_list_entry.view.*

class ImageListAdapter(private val mValues:ArrayList<NASAImageListItem>, private val mListener:ImageListFragment.OnImageListFragmentInteractionListener?):RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int) {
        holder.mListEntryTV!!.text = mValues[position].nasaId
        holder.mView.setOnClickListener { mListener?.onImageListFragmentInteraction(position) }
    }

    override fun getItemCount():Int {
        return mValues.size
    }

    inner class ViewHolder(val mView:View):RecyclerView.ViewHolder(mView) {
        var mListEntryTV: TextView? = null

        init {

            mListEntryTV = mView.data1TV as TextView
        }

        override fun toString(): String {
            return super.toString() + " " + mListEntryTV
        }
    }
}
