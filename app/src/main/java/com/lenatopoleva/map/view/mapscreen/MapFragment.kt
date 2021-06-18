package com.lenatopoleva.map.view.mapscreen

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lenatopoleva.map.R
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.view.BackButtonListener
import com.lenatopoleva.map.view.base.BaseFragment
import org.koin.android.ext.android.getKoin

class MapFragment: BaseFragment<AppState>(), BackButtonListener {

    companion object {
        fun newInstance() = MapFragment()
    }

    override val model: MapViewModel by lazy {
        ViewModelProvider(this, getKoin().get()).get(MapViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View {
        val v: View = inflater.inflate(R.layout.fragment_map, parent, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.fragment_map_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.markers -> {
                model.markersMenuItemClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        model.getData()
    }

    override fun handleData(data: List<DataModel>) {
        Toast.makeText(requireContext(), data.first().name.subSequence(0,3), Toast.LENGTH_LONG )
    }

    override fun backPressed(): Boolean = model.backPressed()

}