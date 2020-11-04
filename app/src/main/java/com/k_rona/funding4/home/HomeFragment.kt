package com.k_rona.funding4.home

import android.os.Bundle
import android.os.Handler
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
import com.k_rona.funding4.adapter.MainFundingViewPagerAdapter
import com.k_rona.funding4.adapter.PopularFundingListAdapter
import com.k_rona.funding4.adapter.PopularPlaceListAdapter
import com.k_rona.funding4.adapter.RecommendFundingListAdapter
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.data.MainFunding
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private var mainFundingList: ArrayList<Funding> = arrayListOf()
    private lateinit var mainFundingResponseBody: MainFunding

    // ViewPager 자동 전환 기능을 위한 Timer 선언, Interval 선언
    var timer = Timer()
    private val DELAY_MS: Long = 500
    private val PERIOD_MS: Long = 3000
    var currentPage: Int = 0

    private var recommendFundingList: ArrayList<Funding> = arrayListOf()
    private var recommendResponseBody: ArrayList<Funding> = arrayListOf()

    private var popularPlaceList: ArrayList<LodgingPlace> = arrayListOf()
    private var popularPlaceResponseBody: ArrayList<LodgingPlace> = arrayListOf()

    private var popularFundingList: ArrayList<Funding> = arrayListOf()
    private var popularFundingResponseBody: ArrayList<Funding> = arrayListOf()

    lateinit var recommendFundingRecyclerView: RecyclerView
    lateinit var recommendFundingAdapter: RecyclerView.Adapter<*>
    lateinit var recommendFundingManager: RecyclerView.LayoutManager

    lateinit var popularPlaceRecyclerView: RecyclerView
    lateinit var popularPlaceAdapter: RecyclerView.Adapter<*>
    lateinit var popularPlaceManager: RecyclerView.LayoutManager

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
        recommendFundingAdapter =
            RecommendFundingListAdapter(recommendFundingList, requireContext())

        recommendFundingRecyclerView =
            view.findViewById<RecyclerView>(R.id.home_recommend_funding_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = recommendFundingManager
                adapter = recommendFundingAdapter
            }

        popularFundingManager = LinearLayoutManager(requireContext())
        popularFundingAdapter = PopularFundingListAdapter(popularFundingList, requireContext())

        popularPlaceRecyclerView =
            view.findViewById<RecyclerView>(R.id.popular_funding_list).apply {
                setHasFixedSize(true)
                layoutManager = popularFundingManager
                adapter = popularFundingAdapter
            }

        popularPlaceManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        popularPlaceAdapter = PopularPlaceListAdapter(popularPlaceList, requireContext())

        popularPlaceRecyclerView =
            view.findViewById<RecyclerView>(R.id.home_popular_place_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = popularPlaceManager
                adapter = popularPlaceAdapter
            }

        getMainFundingList()
        getRecommendFundingList()
        getPopularFundingList()
        getPopularPlaceList()
    }

    override fun onResume() {
        getRecommendFundingList()
        getPopularFundingList()
        getPopularPlaceList()
        timer = Timer()

        super.onResume()
    }
    override fun onStop() {
        super.onStop()

        timer.cancel()
    }

    private fun getMainFundingList() {
        retrofitService.requestMainFundingList().enqueue(object : Callback<ArrayList<MainFunding>> {
            override fun onFailure(call: Call<ArrayList<MainFunding>>, t: Throwable) {
                Log.d("getMainFundingList()", "실패!")
            }

            override fun onResponse(call: Call<ArrayList<MainFunding>>, response: Response<ArrayList<MainFunding>>) {
                if(response.code() == 200 && response.body() != null && main_funding_viewpager != null){
                    mainFundingResponseBody = response.body()!![0]
                    mainFundingList.addAll(mainFundingResponseBody.main_funding)

                    main_funding_viewpager.adapter = MainFundingViewPagerAdapter(context!!, mainFundingList)
                    main_funding_viewpager.currentItem = 0

                    // 자동 전환 View Pager 동작을 위한 Handler 객체 + 동작부
                    val handler = Handler()
                    val updateTask: Runnable =
                        Runnable {
                            if (currentPage == mainFundingList.size) {
                                currentPage = 0
                            }
                            if (main_funding_viewpager != null) {
                                main_funding_viewpager.setCurrentItem(currentPage++, true)
                            }
                        }
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            handler.post(updateTask)
                        }
                    }, DELAY_MS, PERIOD_MS)
                }else{
                    Log.d("Main funding", "Failed!")
                }
            }
        })
    }

    private fun getRecommendFundingList() {
        retrofitService.requestFundingList("like_count", "", "", "")
            .enqueue(object : Callback<ArrayList<Funding>> {
                override fun onResponse(
                    call: Call<ArrayList<Funding>>,
                    response: Response<ArrayList<Funding>>
                ) {
                    if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                        recommendResponseBody.clear()
                        recommendResponseBody = response.body()!!

                        recommendFundingList.addAll(recommendResponseBody)
                        recommendFundingAdapter.notifyDataSetChanged()
                    } else {
                        Log.d(
                            "RecommendFundingList()",
                            "ERROR CODE : " + response.code().toString()
                        )
                        Log.d("RecommendFundingList()", "RESPONSE : " + response.body().toString())
                    }
                }

                override fun onFailure(call: Call<ArrayList<Funding>>, t: Throwable) {
                    Log.e("RecommendFundingList", t.message)
                }
            })
    }

    private fun getPopularFundingList() {
        retrofitService.requestPopularFundingList("hot")
            .enqueue(object : Callback<ArrayList<Funding>> {
                override fun onResponse(
                    call: Call<ArrayList<Funding>>,
                    response: Response<ArrayList<Funding>>
                ) {
                    if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                        popularFundingResponseBody.clear()
                        popularFundingResponseBody = response.body()!!

                        popularFundingList.addAll(popularFundingResponseBody)
                        popularFundingAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<ArrayList<Funding>>, t: Throwable) {
                }
            })
    }

    private fun getPopularPlaceList() {
        retrofitService.requestPopularPlaceList("like_count")
            .enqueue(object : Callback<ArrayList<LodgingPlace>> {
                override fun onResponse(
                    call: Call<ArrayList<LodgingPlace>>,
                    response: Response<ArrayList<LodgingPlace>>
                ) {
                    if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                        popularPlaceResponseBody.clear()
                        popularPlaceResponseBody = response.body()!!

                        popularPlaceList.addAll(popularPlaceResponseBody)
                        popularPlaceAdapter.notifyDataSetChanged()
                    } else {
                        Log.d(
                            "requestPopularPlaceList",
                            "ERROR CODE : " + response.code().toString()
                        )
                        Log.d("requestPopularPlaceList", "RESPONSE : " + response.body().toString())
                    }
                }

                override fun onFailure(call: Call<ArrayList<LodgingPlace>>, t: Throwable) {
                }
            })
    }
}