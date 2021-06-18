package com.lenatopoleva.map.view.mapscreen

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lenatopoleva.map.R
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.view.BackButtonListener
import com.lenatopoleva.map.view.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_map.*
import org.koin.android.ext.android.getKoin

class MapFragment: BaseFragment<AppState>(), BackButtonListener, OnMapReadyCallback {

    private lateinit var gMap: GoogleMap

    companion object {
        fun newInstance() = MapFragment()
    }

    override val model: MapViewModel by lazy {
        ViewModelProvider(this, getKoin().get()).get(MapViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v: View = inflater.inflate(R.layout.fragment_map, parent, false)
        setHasOptionsMenu(true)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Getting map:
        mapView.onCreate(savedInstanceState)
        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapView.getMapAsync(this)
    }

    // Map callback:
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        gMap.addMarker(MarkerOptions()
            .position(sydney)
            .title("Marker in Sydney"))
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        model.getData()
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

    override fun handleData(data: List<DataModel>) {
        Toast.makeText(requireContext(), data.first().name.subSequence(0, 3), Toast.LENGTH_LONG) // here markers should be displayed on map
    }

    override fun backPressed(): Boolean = model.backPressed()

}