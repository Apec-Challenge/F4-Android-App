package com.k_rona.funding4.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.adapter.RecommendFundingListAdapter
import com.k_rona.funding4.adapter.ReviewListAdapter
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.data.Review
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var recommendFundingList: ArrayList<Funding> = arrayListOf()
    private var responseBody: ArrayList<Funding> = arrayListOf()

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

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

        viewManager = GridLayoutManager(requireContext(), 2)
        viewAdapter = RecommendFundingListAdapter(recommendFundingList, requireContext())

        recyclerView =
            view.findViewById<RecyclerView>(R.id.home_recommend_funding_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

        getRecommendFundingList()
    }

    private fun getRecommendFundingList(){
        retrofitService.requestFundingList("like_count")
            .enqueue(object : Callback<ArrayList<Funding>> {
                override fun onResponse(call: Call<ArrayList<Funding>>, response: Response<ArrayList<Funding>>) {
                    if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                        responseBody.clear()
                        responseBody = response.body()!!

                        recommendFundingList.addAll(responseBody)
                        viewAdapter.notifyDataSetChanged()
                    }else{
                        Log.d("RecommendFundingList()", "ERROR CODE : "+ response.code().toString())
                        Log.d("RecommendFundingList()", "RESPONSE : " + response.body().toString())
                    }
                }

                override fun onFailure(call: Call<ArrayList<Funding>>, t: Throwable) {
                    Log.e("RecommendFundingList", t.message)
                }
            })
    }

    private fun getFundingListRank(){

    }
}