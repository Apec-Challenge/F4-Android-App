package com.k_rona.funding4.place

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import kotlinx.android.synthetic.main.fragment_surround_place.*
import noman.googleplaces.NRPlaces
import noman.googleplaces.PlaceType
import noman.googleplaces.PlacesException
import noman.googleplaces.PlacesListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class SurroundPlaceFragment : Fragment(), OnMapReadyCallback, PlacesListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener{

    private var notificationsViewModel: SurroundPlaceViewModel? = null
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private lateinit var placesClient: PlacesClient

    private var surroundLodgingPlaceList: ArrayList<LodgingPlace> = arrayListOf()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val defaultLocation = LatLng(37.56, 126.97)

    private var lastKnownLocation: Location? = null
    private var likelyPlaceNames: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAddresses: Array<String?> = arrayOfNulls(0)
    private var likelyPlaceAttributions: Array<List<*>?> = arrayOfNulls(0)
    private var likelyPlaceLatLngs: Array<LatLng?> = arrayOfNulls(0)

    private var previousMarker: ArrayList<Marker>? = null

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProviders.of(this).get(SurroundPlaceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_surround_place, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        previousMarker = ArrayList<Marker>()
        map?.setOnInfoWindowClickListener(this)
        map?.setOnMarkerClickListener(this)

        Places.initialize(requireContext(), getString(R.string.google_maps_key))
        placesClient = Places.createClient(requireContext())

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        val mapView = view.findViewById<MapView>(R.id.place_info_map)
        mapView.onCreate(savedInstanceState)

        val locationButton =
            (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30);

        mapView.getMapAsync(this)

    }

    override fun onResume() {
        place_info_map.onResume()
        super.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {

        return true
    }

    override fun onInfoWindowClick(marker: Marker?) {
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d("Map", "onMapReady()")
        this.map = googleMap

        this.map?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_place_info_contents,
                    place_info_map, false
                )
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })
        updateLocationUI()
        getDeviceLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            Log.d("Map", "getDeviceLocation() try")
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("Map", "updateLocationUI() try success")
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        Log.d("Test", "Map on")
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )

                        showPlaceInformation(
                            LatLng(
                                lastKnownLocation!!.latitude,
                                lastKnownLocation!!.longitude
                            )
                        )

                        Log.d("Map", "getDeviceLocation() try success")
                        Log.d("Map", lastKnownLocation!!.latitude.toString())
                        Log.d("Map", lastKnownLocation!!.longitude.toString())
                    } else {
                        Log.d("Map", "getDeviceLocation() lastKnownLocation null")
                    }
                } else {
                    Log.d("Map", "getDeviceLocation() task failed")
                    Log.d(TAG, "Current location is null. Using defaults.")
                    Log.e(TAG, "Exception: %s", task.exception)
                    map?.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                    )
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }

        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        val placeResult = placesClient.findCurrentPlace(request)
        placeResult.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val likelyPlaces = task.result

                val count =
                    if (likelyPlaces != null && likelyPlaces.placeLikelihoods.size < M_MAX_ENTRIES) {
                        likelyPlaces.placeLikelihoods.size
                    } else {
                        M_MAX_ENTRIES
                    }
                var i = 0
                likelyPlaceNames = arrayOfNulls(count)
                likelyPlaceAddresses = arrayOfNulls(count)
                likelyPlaceAttributions = arrayOfNulls<List<*>?>(count)
                likelyPlaceLatLngs = arrayOfNulls(count)
                for (placeLikelihood in likelyPlaces?.placeLikelihoods ?: emptyList()) {
                    likelyPlaceNames[i] = placeLikelihood.place.name
                    likelyPlaceAddresses[i] = placeLikelihood.place.address
                    likelyPlaceAttributions[i] = placeLikelihood.place.attributions
                    likelyPlaceLatLngs[i] = placeLikelihood.place.latLng
                    i++
                    if (i > count - 1) {
                        break
                    }
                }

            } else {
                Log.e(TAG, "Exception: %s", task.exception)
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        Log.d("Map", "updateLocationUI()")
        if (map == null) {
            Log.d("Map", "onMapReady() map null")
            return
        }
        try {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
            Log.d("Map", "updateLocationUI() try success")
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        private val TAG = SurroundPlaceFragment::class.java.simpleName
        private const val DEFAULT_ZOOM = 17

        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"

        private const val M_MAX_ENTRIES = 5
    }

    override fun onPlacesFailure(e: PlacesException?) {
    }

    override fun onPlacesStart() {
    }

    override fun onPlacesSuccess(places: MutableList<noman.googleplaces.Place>?) {
        activity?.runOnUiThread(java.lang.Runnable {
            if (places != null) {
                retrofitService.requestSurroundPlaceList()
                    .enqueue(object : Callback<ArrayList<LodgingPlace>> {
                        override fun onResponse(
                            call: Call<ArrayList<LodgingPlace>>,
                            response: Response<ArrayList<LodgingPlace>>
                        ) {
                            if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                                surroundLodgingPlaceList = response.body()!!
                                for (place in places) {
                                    val latLng: LatLng = LatLng(place.latitude, place.longitude)
                                    val markerSnippet: String = getCurrentAddress(latLng)
                                    val markerOptions = MarkerOptions()
                                    markerOptions.position(latLng)
                                    markerOptions.title(place.name)
                                    markerOptions.snippet(markerSnippet)

                                    // 백엔드 API 호출했을 때 응답으로 온 Place List 내에 해당 place 의 ID가 있으면
                                    // 마커 색깔을 다르게 표시 (기본 (정보가 없는 장소) : HUE_MAGENTA)

                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//                                    for(lodgingPlace in surroundLodgingPlaceList){
//                                        if(place.placeId == lodgingPlace.place_id){
//                                        }
//                                    }
                                    val isThisPlaceHaveInfo = surroundLodgingPlaceList.any { it.place_id == place.placeId }

                                    if(isThisPlaceHaveInfo){
                                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                    }

                                    val item: Marker = map!!.addMarker(markerOptions)
//                                    item.tag = place
                                    previousMarker?.add(item)
                                }

                            } else {
                                Log.d("Error lodging place", response.errorBody().toString())
                            }
                        }

                        override fun onFailure(
                            call: Call<ArrayList<LodgingPlace>>,
                            t: Throwable
                        ) {
                            Log.e("Error lodging place", t.message)
                        }
                    })

                val hashSet: HashSet<Marker> = HashSet<Marker>()
                previousMarker?.let { hashSet.addAll(it) }
                previousMarker?.clear()
                previousMarker?.addAll(hashSet)
            }
        })

    }

    override fun onPlacesFinished() {
    }

    private fun showPlaceInformation(location: LatLng) {
        map?.clear() //지도 클리어
        previousMarker?.clear() //지역정보 마커 클리어
        NRPlaces.Builder()
            .listener(this)
            .key(getString(R.string.google_maps_key))
            .latlng(location.latitude, location.longitude) // 현재 위치
            .radius(2000) // 2000 미터 내에서 검색
            .type(PlaceType.LODGING) // 숙박 업소
            .build()
            .execute()
    }

    private fun getCurrentAddress(latlng: LatLng): String {
        // GPS 를 주소로 변환
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses: List<Address>
        addresses = try {
            geocoder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )
        } catch (ioException: IOException) {
            // 네트워크 문제
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        return if (addresses.isEmpty()) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            "주소 미발견"
        } else {
            val address: Address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }
}