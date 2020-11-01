package com.k_rona.funding4.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Review
import com.k_rona.funding4.data.User
import com.k_rona.funding4.funding.FundingDetailActivity
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.place_review_item.view.*
import java.text.SimpleDateFormat

class ReviewListAdapter(
    private val reviewList: ArrayList<Review>,
    private val context: Context
) : RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

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
            holder.view.review_likes.setCompoundDrawables(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_24),null, null, null)
        }else{
            holder.view.review_likes.setCompoundDrawables(ContextCompat.getDrawable(context, R.drawable.ic_baseline_favorite_empty_24),null, null, null)
        }

        holder.view.review_created_at.text = reviewCreatedAt

        holder.view.review_rating.rating = reviewList[position].rating
        holder.view.review_content.text = reviewList[position].content
        holder.view.review_writer.text = reviewList[position].user.toString()
        holder.view.review_likes.text = reviewList[position].total_likes.toString()
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}