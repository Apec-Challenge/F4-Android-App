package com.k_rona.funding4.funding.funding_detail_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.k_rona.funding4.R
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.fragment_funding_ppe.*

class FundingPPEFragment : Fragment() {
    private var fundingHandSanitizer: Int? = null
    private var fundingTemperatureCheck: Int? = null
    private var fundingPersonHygiene: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fundingHandSanitizer = it.getInt("funding_hand_sanitizer")
            fundingTemperatureCheck = it.getInt("funding_temperature_check")
            fundingPersonHygiene = it.getInt("funding_person_hygiene")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_funding_ppe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        funding_hand_sanitizer.progress = fundingHandSanitizer!!
        funding_hygiene.progress = fundingPersonHygiene!!
        funding_temperature.progress = fundingTemperatureCheck!!

        funding_hand_sanitizer.setOnTouchListener { view, motionEvent -> true }
        funding_hygiene.setOnTouchListener { view, motionEvent -> true }
        funding_temperature.setOnTouchListener { view, motionEvent -> true }

    }
}