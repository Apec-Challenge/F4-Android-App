package com.k_rona.funding4.place

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import kotlinx.android.synthetic.main.activity_funding_detail.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class PlaceDetailActivity : AppCompatActivity() {

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_detail)

        val placeDetail: LodgingPlace = intent.getSerializableExtra("place_object") as LodgingPlace
        val placePPE =
            (((placeDetail.body_temperature_check.toFloat() + placeDetail.hand_sanitizer.toFloat() + placeDetail.person_hygiene).toFloat() / 3) * 100).roundToInt() / 100f

        Glide.with(this)
            .load(placeDetail.place_image)
            .centerCrop()
            .thumbnail(0.1f)
            .into(place_image)

        place_title.text = placeDetail.title
        place_address.text = placeDetail.address
        place_description.text = placeDetail.description


        place_hand_sanitizer.progress = placeDetail.hand_sanitizer
        place_hygiene.progress = placeDetail.person_hygiene
        place_temperature.progress = placeDetail.body_temperature_check

        place_hand_sanitizer.isClickable = false
        place_hygiene.isClickable = false
        place_temperature.isClickable = false
    }
}