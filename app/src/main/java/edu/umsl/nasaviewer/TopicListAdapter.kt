package edu.umsl.nasaviewer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.topic_list_entry.view.*

class TopicListAdapter(private val mValues:ArrayList<String>, private val mListener:TopicListFragment.OnTopicListFragmentInteractionListener?):RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.topic_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder:ViewHolder, position:Int) {
        holder.mTopicEntryTV?.text = mValues[position]
        holder.mView.setOnClickListener { mListener?.onTopicListFragmentInteraction(position) }
    }

    override fun getItemCount():Int {
        return mValues.size
    }

    inner class ViewHolder( val mView:View):RecyclerView.ViewHolder(mView) {
        var mTopicEntryTV: TextView? = null

        init {

            mTopicEntryTV = mView.topic_entry as TextView
        }

        override fun toString(): String {
            return super.toString() + " " + mTopicEntryTV
        }
    }
}
