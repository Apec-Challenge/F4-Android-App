package com.k_rona.funding4.funding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.funding.funding_detail_tab.FundingBackedListFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingCommentFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingPPEFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingSummaryFragment
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import com.k_rona.funding4.place.PlaceDetailActivity
import kotlinx.android.synthetic.main.activity_funding_detail.*
import kotlinx.android.synthetic.main.activity_funding_detail.funding_description
import kotlinx.android.synthetic.main.activity_funding_detail.funding_thumbnail_image
import kotlinx.android.synthetic.main.activity_funding_detail.funding_title
import kotlinx.android.synthetic.main.funding_recyclerview_item.*
import kotlinx.android.synthetic.main.funding_recyclerview_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FundingDetailActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: FragmentStatePagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

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
        setContentView(R.layout.activity_funding_detail)

        val fundingDetail: Funding = intent.getSerializableExtra("funding_object") as Funding
        Log.d("Funding intent test", fundingDetail.title)

        val fundingDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val fundingEndedAt = fundingDateFormat.format(fundingDetail.ended_at)

        val fundingEndAtDate: Calendar = Calendar.getInstance()
        fundingEndAtDate.time = fundingDetail.ended_at
        val todayDateCal = Calendar.getInstance()

        val fundingEndAtLong = fundingEndAtDate.timeInMillis / 86400000
        val todayDateLong = todayDateCal.timeInMillis / 86400000

        val dDay = (fundingEndAtLong - todayDateLong) + 1

        val achievementRate: Double =
            fundingDetail.funding_amount.toDouble() / fundingDetail.funding_goal_amount.toDouble()

        Glide.with(this)
            .load(fundingDetail.thumbnail_image)
            .centerCrop()
            .thumbnail(0.1f)
            .into(funding_thumbnail_image)

        funding_title.text = fundingDetail.title
        funding_description.text = fundingDetail.description
        funding_like_button.text = fundingDetail.total_likes.toString()
        funding_backed_count.text = fundingDetail.backed_list.size.toString()

        funding_progress.progress = (achievementRate * 100).toInt()
        funding_achievement_rate.text = (achievementRate * 100).toInt().toString() + "%"

        funding_amount.text = "$ " + fundingDetail.funding_amount.toString()
        funding_deadline.text = fundingEndedAt
        funding_remaining_day.text = dDay.toString()

        getFundingPlace(fundingDetail.place)

//        funding_backed_count.text = fundingDetail.backed
//        funding_owner.text = fundingDetail.user

        tabLayout = findViewById(R.id.funding_detail_tab)
        pagerAdapter = PagerAdaper(supportFragmentManager)
        viewPager = this.findViewById(R.id.funding_detail_viewpager)
        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(viewPager)
    }
    private fun getFundingPlace(placeID: String){
        retrofitService.requestFundingPlace(placeID).enqueue(object: Callback<LodgingPlace>{
            override fun onResponse(call: Call<LodgingPlace>, response: Response<LodgingPlace>) {
                if(response.code() == 200 && response.body() != null){
                    val fundingPlace: LodgingPlace = response.body()!!
                    funding_place_title.text = fundingPlace.title
                    funding_place_address.text = fundingPlace.address

                    funding_place_detail.setOnClickListener {
                        val intent = Intent(applicationContext, PlaceDetailActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("place_object", fundingPlace)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<LodgingPlace>, t: Throwable) {
            }
        })
    }
}


private const val TAB_SUMMARY = 0
private const val TAB_BACKED = 1
private const val TAB_PPE = 2
private const val TAB_COMMENT = 3

class PagerAdaper(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = 4

    override fun getItem(position: Int): Fragment {
        when (position) {
            TAB_SUMMARY -> {
                val fundingSummaryFragment = FundingSummaryFragment()
                fundingSummaryFragment.arguments = Bundle().apply {
//                    putInt(ARG_OBJECT, position + 1)
                }
                return fundingSummaryFragment
            }

            TAB_BACKED -> {
                val fundingBackedListFragment = FundingBackedListFragment()
                fundingBackedListFragment.arguments = Bundle().apply {

                }
                return fundingBackedListFragment
            }

            TAB_PPE -> {
                val fundingPPEFragment = FundingPPEFragment()
                fundingPPEFragment.arguments = Bundle().apply {

                }
                return fundingPPEFragment
            }

            TAB_COMMENT -> {
                val fundingCommentFragment = FundingCommentFragment()
                fundingCommentFragment.arguments = Bundle().apply {

                }
                return fundingCommentFragment
            }

            else -> {
                val fundingSummaryFragment = FundingSummaryFragment()
                fundingSummaryFragment.arguments = Bundle().apply {

                }
                return fundingSummaryFragment
            }
        }
    }

    private val tabTitle : ArrayList<String> = arrayListOf("Summary", "Backed", "PPE", "Comment")

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitle[position]
    }
}

