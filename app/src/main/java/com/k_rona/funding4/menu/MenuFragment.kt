package com.k_rona.funding4.menu

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.k_rona.funding4.R
import com.k_rona.funding4.funding.CreateFundingActivity
import com.k_rona.funding4.user.ChargeMoneyActivity
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.funding_viewpager_item.view.*

class MenuFragment : Fragment() {

    private lateinit var viewModel: MenuViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MenuViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(R.drawable.imac)
            .centerCrop()
            .thumbnail(0.1f)
            .into(menu_banner)

        card_charge_money.setOnClickListener {
            startActivity(Intent(context, ChargeMoneyActivity::class.java))
        }

        card_create_funding.setOnClickListener {
            startActivity(Intent(context, CreateFundingActivity::class.java))
        }
    }
}