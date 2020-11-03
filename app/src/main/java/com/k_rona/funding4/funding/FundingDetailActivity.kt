package com.k_rona.funding4.funding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.k_rona.funding4.data.User
import com.k_rona.funding4.funding.funding_detail_tab.FundingBackedListFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingCommentFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingPPEFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingContentFragment
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import com.k_rona.funding4.place.PlaceDetailActivity
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_funding_detail.*
import kotlinx.android.synthetic.main.activity_funding_detail.funding_description
import kotlinx.android.synthetic.main.activity_funding_detail.funding_thumbnail_image
import kotlinx.android.synthetic.main.activity_funding_detail.funding_title
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

    private lateinit var fundingDetail: Funding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funding_detail)

        Paper.init(this)
        val userProfile: User? = Paper.book().read("user_profile")

        fundingDetail = intent.getSerializableExtra("funding_object") as Funding
        Log.d("Funding intent test", fundingDetail.title)

        val isAlreadyLikedFunding =
            fundingDetail.user_likes.any { it == userProfile?.pk }

        funding_like_heart.isActivated = isAlreadyLikedFunding

        funding_like_heart.setOnClickListener {
            if ( // 좋아요가 안 눌린 상태면 좋아요 반영 동작
                !funding_like_heart.isActivated
            ) {
                funding_like_heart.isActivated = true
                funding_like_count.text =
                    (funding_like_count.text.toString().toInt() + 1).toString()
            } else { // 좋아요가 이미 눌린 상태면 좋아요 취소 동작
                funding_like_heart.isActivated = false
                funding_like_count.text =
                    (funding_like_count.text.toString().toInt() - 1).toString()
            }
            // 서버 단에서는 자동으로 반영/취소 여부를 결정할 수 있기 때문에 같은 요청으로 전달
            Log.d("funding Detail", fundingDetail.id.toString())
            Log.d("funding Detail", userProfile!!.nickname)

            noticeUserPushedLikeButton(userProfile!!.nickname, fundingDetail.id)
        }

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
        funding_like_count.text = fundingDetail.total_likes.toString()
        funding_backed_count.text = fundingDetail.backed_list.size.toString()
        funding_owner.text = fundingDetail.owner_username

        funding_progress.progress = (achievementRate * 100).toInt()
        funding_achievement_rate.text = (achievementRate * 100).toInt().toString() + "%"

        funding_amount.text = "$ " + fundingDetail.funding_amount.toString()
        funding_deadline.text = fundingEndedAt
        funding_remaining_day.text = dDay.toString()

        linkFundingPlace(fundingDetail.place)

//        funding_backed_count.text = fundingDetail.backed
//        funding_owner.text = fundingDetail.user

        tabLayout = findViewById(R.id.funding_detail_tab)
        pagerAdapter = PagerAdaper(supportFragmentManager)
        viewPager = this.findViewById(R.id.funding_detail_viewpager)
        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(viewPager)
    }

    private fun linkFundingPlace(place: LodgingPlace) {
        funding_place_title.text = place.title
        funding_place_address.text = place.address

        funding_place_detail.setOnClickListener {
            val intent = Intent(applicationContext, PlaceDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("place_object", place)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    private val TAB_SUMMARY = 0
    private val TAB_BACKED = 1
    private val TAB_PPE = 2
    private val TAB_COMMENT = 3

    inner class PagerAdaper(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = 4

        override fun getItem(position: Int): Fragment {
            when (position) {
                TAB_SUMMARY -> {
                    val fundingContentFragment = FundingContentFragment()
                    fundingContentFragment.arguments = Bundle().apply {
                        putString("funding_content_image", fundingDetail.content_image)
                        putString("funding_content_text", fundingDetail.content_text)
                    }
                    return fundingContentFragment
                }

                TAB_BACKED -> {
                    val fundingBackedListFragment = FundingBackedListFragment()
                    fundingBackedListFragment.arguments = Bundle().apply {
                        putStringArrayList("funding_backed_list", fundingDetail.backed_list)
                    }
                    return fundingBackedListFragment
                }

                TAB_PPE -> {
                    val fundingPPEFragment = FundingPPEFragment()
                    fundingPPEFragment.arguments = Bundle().apply {
                        putInt("funding_hand_sanitizer", fundingDetail.place.hand_sanitizer)
                        putInt("funding_temperature_check", fundingDetail.place.body_temperature_check)
                        putInt("funding_person_hygiene", fundingDetail.place.person_hygiene)
                    }
                    return fundingPPEFragment
                }

                TAB_COMMENT -> {
                    val fundingCommentFragment = FundingCommentFragment()
                    fundingCommentFragment.arguments = Bundle().apply {
                        putSerializable("funding_comment_list", fundingDetail.comment_list)
                        putSerializable("funding_id", fundingDetail.id)
                    }
                    return fundingCommentFragment
                }

                else -> {
                    val fundingContentFragment = FundingContentFragment()
                    fundingContentFragment.arguments = Bundle().apply {
                    }
                    return fundingContentFragment
                }
            }
        }

        private val tabTitle: ArrayList<String> = arrayListOf("Summary", "Backed", "PPE", "Comment")

        override fun getPageTitle(position: Int): CharSequence? {
            return tabTitle[position]
        }
    }

    private fun noticeUserPushedLikeButton(nickname: String, fundingID: Int) {
        retrofitService.requestFundingLikeButtonPushed(nickname, fundingID)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() == 200) {
                        Log.d("UserPushedLikeButton()", "Like button success!")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {

                }
            })
    }
}




