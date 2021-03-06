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

class FundingListAdapter(
    private val fundingList: ArrayList<Funding>,
    private val context: Context
) : RecyclerView.Adapter<FundingListAdapter.ViewHolder>() {

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
    ): FundingListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.funding_recyclerview_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FundingListAdapter.ViewHolder, position: Int) {
        val achievementRate: Double =
            fundingList[position].funding_amount.toDouble() / fundingList[position].funding_goal_amount.toDouble()

        Glide.with(holder.view)
            .load(fundingList[position].thumbnail_image)
            .override(600, 400)
            .centerCrop()
            .thumbnail(0.1f)
            .into(holder.view.funding_thumbnail_image)

        holder.view.funding_title.text = fundingList[position].title
        holder.view.funding_description.text = fundingList[position].description
        holder.view.funding_progress_bar.progress = (achievementRate * 100).toInt()
        holder.view.funding_progress_text.text = (achievementRate * 100).toInt().toString()
    }

    override fun getItemCount(): Int {
        return fundingList.size
    }
}