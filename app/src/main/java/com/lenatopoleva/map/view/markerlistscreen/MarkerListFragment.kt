package com.lenatopoleva.map.view.markerlistscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lenatopoleva.map.R
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.view.BackButtonListener
import com.lenatopoleva.map.view.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_marker_list.*
import org.koin.android.ext.android.getKoin

class MarkerListFragment: BaseFragment<AppState>(), BackButtonListener {

    companion object {
        fun newInstance() = MarkerListFragment()
    }

    override val model: MarkerListViewModel by lazy {
        ViewModelProvider(this, getKoin().get()).get(MarkerListViewModel::class.java)
    }

    private val observer = Observer<AppState> { renderData(it)  }
    private var adapter: MarkerListAdapter? = null
    private val onListItemClickListener: MarkerListAdapter.OnListItemClickListener =
        object : MarkerListAdapter.OnListItemClickListener {
            override fun onItemClick(data: DataModel) {
                // do smth
            }
        }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        val v: View = inflater.inflate(R.layout.fragment_marker_list, parent, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.subscribe().observe(viewLifecycleOwner, observer)
        model.openDialogFragmentLiveData.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled().let { markerItem ->
                println("OPEN dialog fragment")
                openDialogFragment(markerItem?.listIndex, markerItem?.name, markerItem?.annotation)
            }
        })
        model.saveEditMarkerLiveData.observe(viewLifecycleOwner, Observer { event ->
            event.peekContent().let { position ->
                adapter?.notifyItemChanged(position)
            }
        })
        model.removeItemLiveData.observe(viewLifecycleOwner, Observer { event ->
            event.peekContent().let { position ->
                adapter?.notifyItemRemoved(position)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        model.getData()
    }

    override fun handleData(data: List<DataModel>) {
        model.handleData(data)
        if (adapter == null) {
            markers_recyclerview.layoutManager = LinearLayoutManager(context)
            adapter = MarkerListAdapter(onListItemClickListener, model)
            markers_recyclerview.adapter = adapter
        }
    }

    fun openDialogFragment(layoutPosition: Int?, name: String?, annotation: String?) {
        val dialogFragment = MarkerDialogFragment.newInstance(layoutPosition, name, annotation)
        dialogFragment.show(childFragmentManager, "MarkerDialogFragment")
    }

    override fun backPressed(): Boolean = model.backPressed()

}