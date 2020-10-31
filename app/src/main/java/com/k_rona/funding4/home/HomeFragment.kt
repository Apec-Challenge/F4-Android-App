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
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.adapter.PopularPlaceListAdapter
import com.k_rona.funding4.adapter.RecommendFundingListAdapter
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var recommendFundingList: ArrayList<Funding> = arrayListOf()
    private var recommendResponseBody: ArrayList<Funding> = arrayListOf()

    private var popularPlaceList: ArrayList<LodgingPlace> = arrayListOf()
    private var popularResponseBody: ArrayList<LodgingPlace> = arrayListOf()

    lateinit var recommendFundingRecyclerView: RecyclerView
    lateinit var recommendFundingAdapter: RecyclerView.Adapter<*>
    lateinit var recommendFundingManager: RecyclerView.LayoutManager

    lateinit var popularFundingRecyclerView: RecyclerView
    lateinit var popularFundingAdapter: RecyclerView.Adapter<*>
    lateinit var popularFundingManager: RecyclerView.LayoutManager

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

        recommendFundingManager = GridLayoutManager(requireContext(), 2)
        recommendFundingAdapter = RecommendFundingListAdapter(recommendFundingList, requireContext())

        recommendFundingRecyclerView =
            view.findViewById<RecyclerView>(R.id.home_recommend_funding_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = recommendFundingManager
                adapter = recommendFundingAdapter
            }

        popularFundingManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        popularFundingAdapter = PopularPlaceListAdapter(popularPlaceList, requireContext())

        popularFundingRecyclerView =
            view.findViewById<RecyclerView>(R.id.home_popular_place_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = popularFundingManager
                adapter = popularFundingAdapter
            }


        getRecommendFundingList()
        getPopularPlaceList()
    }

    private fun getRecommendFundingList(){
        retrofitService.requestFundingList("like_count")
            .enqueue(object : Callback<ArrayList<Funding>> {
                override fun onResponse(call: Call<ArrayList<Funding>>, response: Response<ArrayList<Funding>>) {
                    if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                        recommendResponseBody.clear()
                        recommendResponseBody = response.body()!!

                        recommendFundingList.addAll(recommendResponseBody)
                        recommendFundingAdapter.notifyDataSetChanged()
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

    private fun getPopularPlaceList(){
        retrofitService.requestPopularPlaceList("like_count").enqueue(object : Callback<ArrayList<LodgingPlace>>{
            override fun onResponse(
                call: Call<ArrayList<LodgingPlace>>,
                response: Response<ArrayList<LodgingPlace>>
            ) {
                if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                    popularResponseBody.clear()
                    popularResponseBody = response.body()!!

                    popularPlaceList.addAll(popularResponseBody)
                    popularFundingAdapter.notifyDataSetChanged()
                }else{
                    Log.d("requestPopularPlaceList", "ERROR CODE : "+ response.code().toString())
                    Log.d("requestPopularPlaceList", "RESPONSE : " + response.body().toString())
                }
            }

            override fun onFailure(call: Call<ArrayList<LodgingPlace>>, t: Throwable) {
            }
        })
    }
}