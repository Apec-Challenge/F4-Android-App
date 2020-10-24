package com.k_rona.funding4.navigation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.k_rona.funding4.R
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var map: GoogleMap

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

        val mapFragment: SupportMapFragment? =
            (activity?.supportFragmentManager?.findFragmentById(R.id.home_near_by_map)) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        // TODO: 2020/10/24 onMapReady() Callback Method 진입이 안되는 현상 발생 
        map = p0!!

        var SEOUL = LatLng(37.56, 126.97)

        var markerOptions: MarkerOptions = MarkerOptions()
        markerOptions
            .position(SEOUL)
            .title("서울")
            .snippet("한국 수도")

        map.addMarker(markerOptions)
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL))

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10F))
    }
}