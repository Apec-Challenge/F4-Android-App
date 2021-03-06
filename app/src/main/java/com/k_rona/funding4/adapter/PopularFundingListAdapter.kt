package com.k_rona.funding4.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.funding.FundingDetailActivity
import kotlinx.android.synthetic.main.funding_recyclerview_item.view.*
import kotlinx.android.synthetic.main.funding_recyclerview_item.view.funding_thumbnail_image
import kotlinx.android.synthetic.main.popular_funding_item.view.*

class PopularFundingListAdapter(
    private val fundingList: ArrayList<Funding>,
    private val context: Context
) : RecyclerView.Adapter<PopularFundingListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val intent = Intent(context, FundingDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("funding_object", fundingList[adapterPosition])
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularFundingListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.popular_funding_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularFundingListAdapter.ViewHolder, position: Int) {
        val achievementRate: Double =
            fundingList[position].funding_amount.toDouble() / fundingList[position].funding_goal_amount.toDouble()

        Glide.with(holder.view)
            .load(fundingList[position].thumbnail_image)
            .override(600, 400)
            .centerCrop()
            .thumbnail(0.1f)
            .into(holder.view.popular_funding_image)

        holder.view.popular_funding_rank.text = (position + 1).toString()
        holder.view.popular_funding_title.text = fundingList[position].title
        holder.view.popular_funding_description.text = fundingList[position].description
        holder.view.popular_funding_progress.text = (achievementRate * 100).toInt().toString() + "% in progress. . ."
    }

    override fun getItemCount(): Int {
        return if (3 > fundingList.size) {
            fundingList.size
        } else {
            3
        }
    }
}