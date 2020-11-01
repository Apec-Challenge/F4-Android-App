package com.k_rona.funding4.place

import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_place_detail.*
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

        val placeDetail: LodgingPlace = intent.getSerializableExtra("place_object") as LodgingPlace
        val placePPE =
            (((placeDetail.body_temperature_check.toFloat() + placeDetail.hand_sanitizer.toFloat() + placeDetail.person_hygiene).toFloat() / 3) * 100).roundToInt() / 100f

        val isAlreadyLikedPlace =
            placeDetail.user_likes.any { it == userProfile?.pk }

        if(isAlreadyLikedPlace){
            place_like_count.setCompoundDrawables(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24),null, null, null)
        }else{
            place_like_count.setCompoundDrawables(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_empty_24),null, null, null)
        }

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
                if (userProfile != null) {  // 사용자 정보가 유효하면
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
                    if(response.code() == 201 && response.body() != null){
                        Toast.makeText(this@PlaceDetailActivity, "Review Created!", Toast.LENGTH_LONG).show()
                        requestReviewList(placeID = placeID)
                    }else{
                        Log.d("writeReview()", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<Review>, t: Throwable) {
                    Log.e("writeReview()", t.message)
                }
            })
    }
}