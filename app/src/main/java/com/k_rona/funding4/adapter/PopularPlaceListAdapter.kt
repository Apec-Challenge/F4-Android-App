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
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.funding.FundingDetailActivity
import com.k_rona.funding4.place.PlaceDetailActivity
import kotlinx.android.synthetic.main.custom_place_info_contents.view.*
import kotlinx.android.synthetic.main.funding_recyclerview_item.view.*
import kotlinx.android.synthetic.main.popular_place_item.view.*

class PopularPlaceListAdapter(
    private val placeList: ArrayList<LodgingPlace>,
    private val context: Context
) : RecyclerView.Adapter<PopularPlaceListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val intent = Intent(context, PlaceDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("place_object", placeList[adapterPosition - 1])
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PopularPlaceListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.popular_place_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PopularPlaceListAdapter.ViewHolder, position: Int) {

        Glide.with(holder.view)
            .load(placeList[position].place_image)
            .centerCrop()
            .override(600, 400)
            .thumbnail(0.1f)
            .into(holder.view.popular_place_image)

        holder.view.popular_place_title.text = placeList[position].title
    }

    override fun getItemCount(): Int {
        return if(5 > placeList.size){
            placeList.size
        }else{
            5
        }
    }
}