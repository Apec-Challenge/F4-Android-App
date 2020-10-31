package com.k_rona.funding4.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Review
import com.k_rona.funding4.funding.FundingDetailActivity
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

        holder.view.review_created_at.text = reviewCreatedAt

        holder.view.review_rating.rating = reviewList[position].rating
        holder.view.review_content.text = reviewList[position].content
        holder.view.review_writer.text = reviewList[position].user.toString()

    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}