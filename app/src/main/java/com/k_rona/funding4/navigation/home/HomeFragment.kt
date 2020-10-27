package com.k_rona.funding4.navigation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.k_rona.funding4.R
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var googleMap: GoogleMap
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapView = view.findViewById<MapView>(R.id.home_near_by_map)
        mapView.onCreate(savedInstanceState)
        val map = mapView.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap!!

        var SEOUL = LatLng(37.56, 126.97)

        var markerOptions = MarkerOptions()

        markerOptions
            .position(SEOUL)
            .title("서울")
            .snippet("한국 수도")

        this.googleMap.addMarker(markerOptions)
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15F))
    }

    override fun onResume() {
        home_near_by_map.onResume()
        super.onResume()
    }
}