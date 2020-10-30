package com.k_rona.funding4.place

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import kotlinx.android.synthetic.main.activity_place_register.*
import kotlinx.android.synthetic.main.fragment_surround_place.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlaceRegisterActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_place_register)

        submit_button.setOnClickListener {
            when {
                place_name_edit_text.text.toString().isEmpty() -> {
                    place_name_edit_text.error = "This field is required."
                }

                place_description_edit_text.text.toString().isEmpty() -> {
                    place_description_edit_text.error = "This field is required."
                }

                

                else -> {

                }
            }
        }
    }
}