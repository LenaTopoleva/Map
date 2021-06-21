package com.lenatopoleva.map.view.markerlistscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lenatopoleva.map.R
import com.lenatopoleva.map.model.data.DataModel
import kotlinx.android.synthetic.main.marker_item.view.*

class MarkerListAdapter(private var onListItemClickListener: OnListItemClickListener,
private var model: MarkerListViewModel) :
        RecyclerView.Adapter<MarkerListAdapter.RecyclerItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.marker_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(model.getMarkerList()[position])
    }

    override fun getItemCount(): Int {
        return model.getItemCount()
    }

    private fun openDialog(layoutPosition: Int, name: String, annotation: String?) {
        model.markerListEditItemBtnClicked(layoutPosition, name, annotation)
    }

    private fun removeItem(layoutPosition: Int){
        model.markerListRemoveItemBtnClicked(layoutPosition)
    }


    inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            fun bind(data: DataModel) {
                if (layoutPosition != RecyclerView.NO_POSITION) {
                    itemView.marker_name_textview.text = data.name
                    itemView.marker_annotation_textview.text = data.annotation
                    itemView.latitude_textView.text = data.latitude.toString().subSequence(0,7)
                    itemView.longitude_textView.text = data.longitude.toString().subSequence(0,7)

                    itemView.editImageView.setOnClickListener { openDialog(layoutPosition, data.name, data.annotation)}
                    itemView.removeItemImageView.setOnClickListener { removeItem(layoutPosition)}
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