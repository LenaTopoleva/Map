package com.lenatopoleva.map.view.mapscreen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lenatopoleva.map.R
import com.lenatopoleva.map.model.data.AppState
import com.lenatopoleva.map.model.data.DataModel
import com.lenatopoleva.map.utils.location.CurrentLocationGetter
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
    private val observer = Observer<AppState> { renderData(it)  }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?,
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

        //Subscribe on liveData with marker list
        model.subscribe().observe(viewLifecycleOwner, observer)
    }

    // Map callback:
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        // Ask permission:
        getPermission()
    }

    private fun getPermission(){
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            showActualMap()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray,
    ) {
        if (requestCode == 100) {
            if (permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showActualMap()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showActualMap(){
        gMap.getUiSettings().setMyLocationButtonEnabled(true)
        // Enable zoom controls
        gMap.getUiSettings().setZoomControlsEnabled(true)
        // For showing a move to my location button
        gMap.setMyLocationEnabled(true)
        gMap.setOnMapLongClickListener(OnMapLongClickListener { latLng: LatLng ->
            showLocation(latLng.latitude, latLng.longitude, null)
            model.makeAMark(latLng.latitude, latLng.longitude)
        })
        val currLocation = getCurrentLocation()
        showLocation(currLocation?.latitude, currLocation?.longitude, "You are here")
    }

    private fun getCurrentLocation(): Location? {
        val currentLocationGetter = CurrentLocationGetter(requireContext())
        return currentLocationGetter.getCurrLocation()
    }

    private fun showLocation(latitude: Double?, longitude: Double?, name: String?){
        val currentLocation: LatLng;
        if (latitude != null && longitude != null) {
            currentLocation = LatLng(latitude, longitude)
            gMap.addMarker(MarkerOptions()
                .position(currentLocation)
                .title("$name"))
            gMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        } else {
            print("Cant find current location")
        }
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
        for(place in data){
            showLocation(place.latitude, place.longitude, place.name)
        }
    }

    override fun backPressed(): Boolean = model.backPressed()

}