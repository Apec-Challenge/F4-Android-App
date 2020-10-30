package com.k_rona.funding4.funding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.k_rona.funding4.R
import com.k_rona.funding4.funding.funding_detail_tab.FundingBackedListFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingCommentFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingPPEFragment
import com.k_rona.funding4.funding.funding_detail_tab.FundingSummaryFragment
import kotlinx.android.synthetic.main.activity_funding_detail.*

class FundingDetailActivity : AppCompatActivity() {

    private lateinit var pagerAdapter: FragmentStatePagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_funding_detail)

        tabLayout = findViewById(R.id.funding_detail_tab)
        pagerAdapter = PagerAdaper(supportFragmentManager)
        viewPager = this.findViewById(R.id.funding_detail_viewpager)
        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(viewPager)
    }
}

private const val TAB_SUMMARY = 0
private const val TAB_BACKED = 1
private const val TAB_PPE = 2
private const val TAB_COMMENT = 3

class PagerAdaper(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = 4

    override fun getItem(position: Int): Fragment {
        when (position) {
            TAB_SUMMARY -> {
                val fundingSummaryFragment = FundingSummaryFragment()
                fundingSummaryFragment.arguments = Bundle().apply {
//                    putInt(ARG_OBJECT, position + 1)
                }
                return fundingSummaryFragment
            }

            TAB_BACKED -> {
                val fundingBackedListFragment = FundingBackedListFragment()
                fundingBackedListFragment.arguments = Bundle().apply {

                }
                return fundingBackedListFragment
            }

            TAB_PPE -> {
                val fundingPPEFragment = FundingPPEFragment()
                fundingPPEFragment.arguments = Bundle().apply {

                }
                return fundingPPEFragment
            }

            TAB_COMMENT -> {
                val fundingCommentFragment = FundingCommentFragment()
                fundingCommentFragment.arguments = Bundle().apply {

                }
                return fundingCommentFragment
            }

            else -> {
                val fundingSummaryFragment = FundingSummaryFragment()
                fundingSummaryFragment.arguments = Bundle().apply {

                }
                return fundingSummaryFragment
            }
        }
    }

    private val tabTitle : ArrayList<String> = arrayListOf<String>("Summary", "Backed", "PPE", "Comment")

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitle[position]
    }
}

