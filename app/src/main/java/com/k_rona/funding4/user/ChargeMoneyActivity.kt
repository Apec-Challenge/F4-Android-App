package com.k_rona.funding4.user

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Money
import com.k_rona.funding4.data.User
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_charge_money.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChargeMoneyActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_charge_money)

        Paper.init(this)
        val userProfile: User? = Paper.book().read("user_profile")

        if (userProfile != null) {
            requestCurrentMoney("Token " + userProfile.key)
        }

        cancel_charge_button.setOnClickListener {
            finish()
        }

        submit_charge_button.setOnClickListener {
            var money: Int
            if (!charge_amount_edit_text.text.isNullOrEmpty()) {
                money = charge_amount_edit_text.text.toString().toInt()

                if (userProfile != null) {
                    requestChargeMoney("Token " + userProfile.key, money)
                }
            }
        }
    }

    private fun requestCurrentMoney(token: String){
        retrofitService.requestCurrentMoney(token).enqueue(object: Callback<Money>{
            override fun onFailure(call: Call<Money>, t: Throwable) {
                Log.e("requestCurrentMoney()", t.message)
            }

            override fun onResponse(call: Call<Money>, response: Response<Money>) {
                if(response.code() == 200 && response.body() != null){
                    current_money.text = response.body()!!.money.toString()
                }
            }
        })
    }

    private fun requestChargeMoney(token: String, money: Int) {
        Log.d("requestChargeMoney()", token)
        retrofitService.requestChargeMoney(token, Money(money)).enqueue(object : Callback<Money> {
            override fun onFailure(call: Call<Money>, t: Throwable) {
                Log.e("requestChargeMoney()", t.message)
            }

            override fun onResponse(call: Call<Money>, response: Response<Money>) {
                if (response.code() == 200 && response.body() != null) {
                    Log.d("requestChargeMoney()", response.body()!!.money.toString())
                    Toast.makeText(this@ChargeMoneyActivity, "Charging Complete!", Toast.LENGTH_LONG).show()
                    finish()
                } else{
                    Log.d("requestChargeMoney()", response.body().toString())
                    Log.d("requestChargeMoney()", response.code().toString())
                }
            }
        })
    }
}