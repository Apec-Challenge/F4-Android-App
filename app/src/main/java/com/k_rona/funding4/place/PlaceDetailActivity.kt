package com.k_rona.funding4.place

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.adapter.ReviewListAdapter
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.data.Review
import com.k_rona.funding4.data.User
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_funding_detail.*
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.activity_place_detail.place_like_count
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.roundToInt

class PlaceDetailActivity : AppCompatActivity() {

    private var reviewList: ArrayList<Review> = arrayListOf()
    private var responseBody: ArrayList<Review> = arrayListOf()

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

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
        setContentView(R.layout.activity_place_detail)

        Paper.init(this)
        val userProfile: User? = Paper.book().read("user_profile")

        Log.d("user Profile", userProfile!!.nickname)

        val placeDetail: LodgingPlace = intent.getSerializableExtra("place_object") as LodgingPlace
        val placePPE =
            (((placeDetail.body_temperature_check.toFloat() + placeDetail.hand_sanitizer.toFloat() + placeDetail.person_hygiene).toFloat() / 3) * 100).roundToInt() / 100f

        val isAlreadyLikedPlace =
            placeDetail.user_likes.any { it == userProfile?.pk }

        place_like_heart.isActivated = isAlreadyLikedPlace

        viewManager = LinearLayoutManager(this)
        viewAdapter = ReviewListAdapter(reviewList, this)

        recyclerView =
            this.findViewById<RecyclerView>(R.id.review_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

        Glide.with(this)
            .load(placeDetail.place_image)
            .centerCrop()
            .thumbnail(0.1f)
            .into(place_image)

        place_like_count.text = placeDetail.total_likes.toString()

        place_title.text = placeDetail.title
        place_address.text = placeDetail.address
        place_description.text = placeDetail.description

        place_hand_sanitizer.progress = placeDetail.hand_sanitizer
        place_hygiene.progress = placeDetail.person_hygiene
        place_temperature.progress = placeDetail.body_temperature_check

        place_hand_sanitizer.setOnTouchListener { view, motionEvent -> true }
        place_hygiene.setOnTouchListener { view, motionEvent -> true }
        place_temperature.setOnTouchListener { view, motionEvent -> true }

        place_like_heart.setOnClickListener {
            // 좋아요가 안 눌린 상태면 좋아요 반영 동작
            if (!place_like_heart.isActivated) {
                place_like_heart.isActivated = true
                place_like_count.text =
                    (place_like_count.text.toString().toInt() + 1).toString()
            } else { // 좋아요가 이미 눌린 상태면 좋아요 취소 동작
                place_like_heart.isActivated = false
                place_like_count.text =
                    (place_like_count.text.toString().toInt() - 1).toString()
            }
            // 서버 단에서는 자동으로 반영/취소 여부를 결정할 수 있기 때문에 같은 요청으로 전달
            noticeUserPushedLikeButton(userProfile.nickname, placeDetail.place_id)
        }

        review_write_button.setOnClickListener {
            review_form.visibility = View.VISIBLE
        }

        review_cancel_button.setOnClickListener {
            review_form.visibility = View.GONE
        }

        review_submit_button.setOnClickListener {
            if (review_edit_text.text.isNullOrBlank()) {
                review_edit_text.error = "You have to write comment"
            } else {
                // 사용자 정보가 유효하면
                writeReview(
                    userID = userProfile.pk,
                    placeID = placeDetail.place_id,
                    content = review_edit_text.text.toString(),
                    rating = review_rating.rating.toInt()
                )

                Log.d("writeReview()", userProfile.pk.toString())
                Log.d("writeReview()", placeDetail.place_id)
                Log.d("writeReview()", review_edit_text.text.toString())
                Log.d("writeReview()", review_rating.rating.toString())
            }
        }

        requestReviewList(placeID = placeDetail.place_id)
    }

    private fun requestReviewList(placeID: String) {
        retrofitService.requestReviewList(placeID).enqueue(object : Callback<ArrayList<Review>> {
            override fun onResponse(
                call: Call<ArrayList<Review>>,
                response: Response<ArrayList<Review>>
            ) {
                if (response.code() == 200 && !response.body().isNullOrEmpty()) {
                    review_form.visibility = View.GONE

                    responseBody.clear()
                    responseBody = response.body()!!

                    reviewList.addAll(responseBody)
                    viewAdapter.notifyDataSetChanged()

                    val ratingSum: ArrayList<Int> = arrayListOf(0, 0, 0, 0, 0)
                    var totalRatingSum: Int = 0
                    var ratingRatio: ArrayList<Float> = arrayListOf(0F, 0F, 0F, 0F, 0F)

                    for (review in responseBody) {
                        ratingSum[(review.rating - 1).toInt()]++
                        totalRatingSum++
                    }

                    ratingRatio[0] = ((ratingSum[0].toFloat() / totalRatingSum)) * 100
                    ratingRatio[1] = ((ratingSum[1].toFloat() / totalRatingSum)) * 100
                    ratingRatio[2] = ((ratingSum[2].toFloat() / totalRatingSum)) * 100
                    ratingRatio[3] = ((ratingSum[3].toFloat() / totalRatingSum)) * 100
                    ratingRatio[4] = ((ratingSum[4].toFloat() / totalRatingSum)) * 100

                    raiting_1_sum_progress.progress = ratingRatio[0].toInt()
                    raiting_2_sum_progress.progress = ratingRatio[1].toInt()
                    raiting_3_sum_progress.progress = ratingRatio[2].toInt()
                    raiting_4_sum_progress.progress = ratingRatio[3].toInt()
                    raiting_5_sum_progress.progress = ratingRatio[4].toInt()

                }
            }

            override fun onFailure(call: Call<ArrayList<Review>>, t: Throwable) {
            }
        })
    }

    private fun writeReview(placeID: String, userID: Int, content: String, rating: Int) {
        retrofitService.requestPostReview(userID, placeID, content, rating)
            .enqueue(object : Callback<Review> {
                override fun onResponse(call: Call<Review>, response: Response<Review>) {
                    if (response.code() == 201 && response.body() != null) {
                        Toast.makeText(
                            this@PlaceDetailActivity,
                            "Review Created!",
                            Toast.LENGTH_LONG
                        ).show()
                        reviewList.add(0, response.body()!!)
                        viewAdapter.notifyDataSetChanged()
                        review_form.visibility = View.GONE
                    } else {
                        Log.d("writeReview()", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<Review>, t: Throwable) {
                    Log.e("writeReview()", t.message)
                }
            })
    }

    private fun noticeUserPushedLikeButton(nickname: String, placeID: String) {
        retrofitService.requestPlaceLikeButtonPushed(nickname, placeID)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() == 200) {
                        Log.d("UserPushedLikeButton()", "Like button success!")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("UserPushedLikeButton()", t.message)
                }
            })
    }
}