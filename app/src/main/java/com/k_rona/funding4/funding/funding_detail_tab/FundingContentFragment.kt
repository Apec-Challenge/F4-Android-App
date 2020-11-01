package com.k_rona.funding4.funding.funding_detail_tab

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.k_rona.funding4.R
import kotlinx.android.synthetic.main.fragment_funding_content.*

class FundingContentFragment : Fragment() {
    private var fundingContentImage: String? = null
    private var fundingContentText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fundingContentImage = it.getString("funding_content_image")
            fundingContentText = it.getString("funding_content_text")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_funding_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(view)
            .load(fundingContentImage)
            .override(600, 400)
            .centerCrop()
            .thumbnail(0.1f)
            .into(funding_content_image)

        funding_content_text.text = fundingContentText
    }
}