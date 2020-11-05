package com.k_rona.funding4.funding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.data.User
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_funding_payment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FundingPaymentActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_funding_payment)

        Paper.init(this)
        val userProfile: User? = Paper.book().read("user_profile")

        val bundle: Bundle = intent.extras!!

        val fundingDetail: Funding = bundle.getSerializable("funding_detail") as Funding

        val placeID: String = fundingDetail.place.place_id
        val fundingPrice: Int = fundingDetail.funding_price
        val token: String = "Token " + userProfile!!.key

        Glide.with(this)
            .load(fundingDetail.thumbnail_image)
            .centerCrop()
            .thumbnail(0.1f)
            .into(payment_funding_image)

        Log.d("FundingPaymentActivity", placeID)
        Log.d("FundingPaymentActivity", fundingPrice.toString())
        Log.d("FundingPaymentActivity", token)

        payment_funding_title.text = fundingDetail.title
        payment_funding_description.text = fundingDetail.description
        payment_funding_option.text = fundingPrice.toString()

        submit_payment_button.setOnClickListener {
            requestFundPlace(token, placeID, fundingPrice)
        }

        cancel_payment_button.setOnClickListener {
            finish()
        }
    }

    private fun requestFundPlace(token: String, placeID: String, money: Int) {
        retrofitService.requestFundPlace(token = token, placeID = placeID, money = money).enqueue(object: Callback<Void>{
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("requestFundPlace()", t.message)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if(response.code() == 201){
                    Log.d("requestFundPlace()", "Funding Success!")
                }

                Log.d("requestFundPlace()", response.body().toString())
            }
        })
    }
}