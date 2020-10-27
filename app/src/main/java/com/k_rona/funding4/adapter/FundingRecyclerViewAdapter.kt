package com.k_rona.funding4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Funding
import kotlinx.android.synthetic.main.funding_recyclerview_item.view.*

class FundingRecyclerViewAdapter(
    private val fundingList: ArrayList<Funding>,
    private val context: Context
): RecyclerView.Adapter<FundingRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view), View.OnClickListener{
        init{
            view.setOnClickListener(this)

        }
        override fun onClick(v: View?) {
            // TODO: 2020/10/27 Funding Model 구체화 후 구현
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FundingRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.funding_recyclerview_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FundingRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.view.funding_title.text = fundingList[position].title
        holder.view.funding_description.text = fundingList[position].content

        // TODO: 2020/10/27 Funding Model 구체화 후 구현
    }

    override fun getItemCount(): Int {
        return fundingList.size
    }
}