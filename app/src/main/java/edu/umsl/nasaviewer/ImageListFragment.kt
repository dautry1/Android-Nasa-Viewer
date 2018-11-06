package edu.umsl.nasaviewer

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ImageListFragment : Fragment() {

    private var mColumnCount = 1
    private var mListener: OnImageListFragmentInteractionListener? = null
    var mImageListAdapter: ImageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_topic_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            if (mColumnCount <= 1) {
                view.layoutManager = LinearLayoutManager(context)
            } else {
                view.layoutManager = GridLayoutManager(context, mColumnCount)
            }
            view.adapter = ImageListAdapter(NASAData.mTheNASAImageList!!, mListener)
            val adapter = view.adapter
            mImageListAdapter = adapter as ImageListAdapter
        }
        return view
    }

    interface OnImageListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onImageListFragmentInteraction(position: Int)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnImageListFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement ImageListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {

        // TODO: Customize parameter argument names
        private const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): ImageListFragment {
            val fragment = ImageListFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
