package com.k_rona.funding4.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Review
import com.k_rona.funding4.data.User
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.place_review_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat

class ReviewListAdapter(
    private val reviewList: ArrayList<Review>,
    private val context: Context
) : RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        init{
            view.review_like_button.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            Paper.init(context)
            val userProfile: User? = Paper.book().read("user_profile")

            if ( // 좋아요가 안 눌린 상태면 좋아요 반영 동작
                view.review_like_button.compoundDrawables == ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_favorite_empty_24
                )
            ) {
                view.review_like_button.setCompoundDrawables(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_favorite_24
                    ), null, null, null
                )
            } else{ // 좋아요가 이미 눌린 상태면 좋아요 취소 동작
                view.review_like_button.setCompoundDrawables(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_baseline_favorite_empty_24
                    ), null, null, null
                )
            }


            if (userProfile != null) {
                noticeUserPushedLikeButton(userProfile.nickname, reviewList[adapterPosition - 1].id)
            }else{
                Toast.makeText(context, "User invalid", Toast.LENGTH_LONG).show()
                Log.d("review onClick()", "User Info Invalid!")
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_review_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewListAdapter.ViewHolder, position: Int) {

        val reviewDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val reviewCreatedAt = reviewDateFormat.format(reviewList[position].created_at)

        Paper.init(context)
        val userProfile: User? = Paper.book().read("user_profile")

        val isAlreadyLikedReview =
            reviewList[position].user_likes.any { it == userProfile?.pk }

        if(isAlreadyLikedReview){
            holder.view.review_like_button.setCompoundDrawables(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24),null, null, null)
        }else{
            holder.view.review_like_button.setCompoundDrawables(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_empty_24),null, null, null)
        }

        holder.view.review_created_at.text = reviewCreatedAt

        holder.view.review_rating.rating = reviewList[position].rating
        holder.view.review_content.text = reviewList[position].content
        holder.view.review_writer.text = reviewList[position].user.toString()
        holder.view.review_like_button.text = reviewList[position].total_likes.toString()
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    private fun noticeUserPushedLikeButton(nickname: String, placeID: Int) {
        val gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Server.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.requestReviewLikeButtonPushed(nickname, placeID).enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(context, "Like!", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {

            }
        })
    }
}