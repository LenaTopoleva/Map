package com.lenatopoleva.map.view.markerlistscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lenatopoleva.map.R
import com.lenatopoleva.map.model.data.DataModel
import kotlinx.android.synthetic.main.marker_item.view.*

class MarkerListAdapter(private var onListItemClickListener: OnListItemClickListener, private var data: List<DataModel>) :
        RecyclerView.Adapter<MarkerListAdapter.RecyclerItemViewHolder>() {

        fun setData(data: List<DataModel>) {
            this.data = data
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
            return RecyclerItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.marker_item, parent, false) as View
            )
        }

        override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
            holder.bind(data.get(position))
        }

        override fun getItemCount(): Int {
            return data.size
        }

        inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bind(data: DataModel) {
                if (layoutPosition != RecyclerView.NO_POSITION) {
                    itemView.marker_name_textview.text = data.name
                    itemView.marker_annotation_textview.text = data.annotation
                    itemView.latitude_textView.text = data.latitude.toString().subSequence(0,7)
                    itemView.longitude_textView.text = data.longitude.toString().subSequence(0,7)

                    itemView.setOnClickListener { openInNewWindow(data) }
                }
            }
        }

        private fun openInNewWindow(listItemData: DataModel) {
            onListItemClickListener.onItemClick(listItemData)
        }

        interface OnListItemClickListener {
            fun onItemClick(data: DataModel)
        }
    }