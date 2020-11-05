package com.k_rona.funding4.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.MoneyCharge
import com.k_rona.funding4.data.User
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_charge_money.*
import kotlinx.android.synthetic.main.fragment_menu.*
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

    private fun requestChargeMoney(token: String, money: Int) {
        Log.d("requestChargeMoney()", token)
        retrofitService.requestChargeMoney(token, MoneyCharge(money)).enqueue(object : Callback<MoneyCharge> {
            override fun onFailure(call: Call<MoneyCharge>, t: Throwable) {
                Log.e("requestChargeMoney()", t.message)
            }

            override fun onResponse(call: Call<MoneyCharge>, response: Response<MoneyCharge>) {
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