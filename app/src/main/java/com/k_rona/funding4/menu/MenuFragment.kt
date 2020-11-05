package com.k_rona.funding4.menu

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.User
import com.k_rona.funding4.data.UserInfo
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import com.k_rona.funding4.user.ChargeMoneyActivity
import io.paperdb.Paper
import kotlinx.android.synthetic.main.fragment_menu.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Paper.init(requireContext())
        val userProfile: User = Paper.book().read("user_profile")

        Glide.with(requireContext())
            .load(R.drawable.imac)
            .centerCrop()
            .thumbnail(0.1f)
            .into(menu_banner)

        getUserInfo(userProfile.pk)

        user_nickname.text = userProfile.nickname
        user_email.text = userProfile.email


        card_charge_money.setOnClickListener {
            startActivity(Intent(context, ChargeMoneyActivity::class.java))
        }

//        card_create_funding.setOnClickListener {
//            startActivity(Intent(context, CreateFundingActivity::class.java))
//        }
    }

    private fun getUserInfo(userID: Int){
        retrofitService.requestUserInfo(userID).enqueue(object: Callback<UserInfo>{
            override fun onFailure(call: Call<UserInfo>, t: Throwable) {

            }

            override fun onResponse(call: Call<UserInfo>, response: Response<UserInfo>) {

            }
        })
    }
}