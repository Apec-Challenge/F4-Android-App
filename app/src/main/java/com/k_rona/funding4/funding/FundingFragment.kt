package com.k_rona.funding4.funding

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.adapter.FundingListAdapter
import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import kotlinx.android.synthetic.main.fragment_funding.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FundingFragment : Fragment() {

    private lateinit var dashboardViewModel: FundingViewModel

    private var fundingList: ArrayList<Funding> = ArrayList()
    private var userInputKeyword: String = ""

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    private val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

    // Sort 를 위한 Filter Option 상수 선언
    companion object {
        const val SORT_RECOMMEND = 0  // 추천 순 (좋아요 개수 순)
        const val SORT_LATEST = 1  // 최신 순
        const val SORT_CLOSING_LIMIT = 2  // 마감 임박 순
        const val SORT_POPULAR = 3  // 인기 순 (참여자 순)
        const val SORT_FUNDING_AMOUNT = 4  // 펀딩 금액 순 (달성율 순)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProviders.of(this).get(FundingViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_funding, container, false)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewManager = LinearLayoutManager(context)
        viewAdapter = FundingListAdapter(
            fundingList,
            requireContext()
        )

        recyclerView =
            view.findViewById<RecyclerView>(R.id.funding_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

        search_funding_edit_text.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                // Enter Action
                userInputKeyword = search_funding_edit_text.text.toString() + " "

                getFundingList(userInputKeyword)

                true
            } else if (search_funding_edit_text.text.toString() == "") {
                search_funding_edit_text.error = "검색할 키워드를 입력해주세요"
                true
            } else {
                false
            }
        }

        select_filter_button.setOnClickListener {
            val sortFilter = arrayOf(
                "Sort by Recommendation",
                "Sort by latest order",
                "Sort by Closing Limits",
                "Sort by Popularity",
                "Sorting by funding amount"
            )

            val userInputKeyword = search_funding_edit_text.text.toString() + " "
            val alertDialog = AlertDialog.Builder(
                context,
                android.R.style.Theme_DeviceDefault_Light_Dialog_Alert
            )
            alertDialog.setTitle("Set Filter")
                .setItems(sortFilter, DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        SORT_RECOMMEND ->{
                            select_filter_button.text = getString(R.string.filter_recommend)
                            getFundingList(keyword = userInputKeyword, filter = SORT_RECOMMEND)
                        }
                        SORT_LATEST ->{
                            select_filter_button.text = getString(R.string.filter_latest)
                            getFundingList(keyword = userInputKeyword, filter = SORT_LATEST)
                        }
                        SORT_CLOSING_LIMIT ->{
                            select_filter_button.text = getString(R.string.filter_closing_limit)
                            getFundingList(keyword = userInputKeyword, filter = SORT_CLOSING_LIMIT)
                        }
                        SORT_POPULAR ->{
                            select_filter_button.text = getString(R.string.filter_popular)
                            getFundingList(keyword = userInputKeyword, filter = SORT_POPULAR)
                        }
                        SORT_FUNDING_AMOUNT ->{
                            select_filter_button.text = getString(R.string.filter_funding_amount)
                            getFundingList(keyword = userInputKeyword, filter = SORT_FUNDING_AMOUNT)
                        }
                        else->{
                            select_filter_button.text = getString(R.string.filter)
                        }
                    }
                })
                .setCancelable(true)
                .show()
        }
    }

    private fun getFundingList(
        keyword: String = "",
        filter: Int = 0
    ) {

    }


}