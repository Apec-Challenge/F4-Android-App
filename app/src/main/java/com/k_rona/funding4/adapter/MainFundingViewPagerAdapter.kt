package com.k_rona.funding4.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.k_rona.funding4.R
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.funding.FundingDetailActivity
import kotlinx.android.synthetic.main.activity_funding_detail.view.*
import kotlinx.android.synthetic.main.fragment_funding.*
import kotlinx.android.synthetic.main.funding_viewpager_item.view.*


class MainFundingViewPagerAdapter(val context: Context, val mainFundingList: ArrayList<Funding>) :
    PagerAdapter() {
    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.funding_viewpager_item, container, false)

        view.main_funding_title.text = mainFundingList[position].title
        view.main_funding_summary.text = mainFundingList[position].description

        Glide.with(context)
            .load(mainFundingList[position].thumbnail_image)
            .centerCrop()
            .thumbnail(0.1f)
            .into(view.main_funding_image)

        view.main_funding_image.setColorFilter(Color.parseColor("#88000000"))

        view.setOnClickListener {
            val intent = Intent(context, FundingDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("funding_object", mainFundingList[position])
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        return mainFundingList.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View?)
    }
}