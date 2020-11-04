package com.k_rona.funding4.funding.funding_detail_tab

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.adapter.BackedListAdapter
import com.k_rona.funding4.adapter.FundingCommentListAdapter
import com.k_rona.funding4.data.FundingComment
import com.k_rona.funding4.data.User
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_place_detail.*
import kotlinx.android.synthetic.main.fragment_funding_comment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FundingCommentFragment : Fragment() {
    private var commentList: ArrayList<FundingComment> = arrayListOf()
    private var fundingID: Int = -1

    private lateinit var responseBody: FundingComment

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            commentList = it.getSerializable("funding_comment_list") as ArrayList<FundingComment>
            fundingID = it.getInt("funding_id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_funding_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Paper.init(context)
        val userProfile: User? = Paper.book().read("user_profile")

        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = FundingCommentListAdapter(commentList, requireContext())

        recyclerView =
            view.findViewById<RecyclerView>(R.id.comment_list_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }

        comment_write_button.setOnClickListener {
            comment_form.visibility = View.VISIBLE
        }

        comment_cancel_button.setOnClickListener {
            comment_form.visibility = View.GONE
        }

        comment_submit_button.setOnClickListener {
            if (comment_edit_text.text.isNullOrBlank()) {
                comment_edit_text.error = "You have to write comment"
            } else {
                // 사용자 정보가 유효하면
                if (userProfile != null) {
                    writeComment(
                        nickname = userProfile.nickname,
                        fundingID = fundingID,
                        content = comment_edit_text.text.toString()
                    )
                }

            }
        }
    }

    private fun writeComment(nickname: String, fundingID: Int, content:String){
        retrofitService.requestPostComment(nickname, fundingID, content).enqueue(object: Callback<FundingComment>{
            override fun onResponse(
                call: Call<FundingComment>,
                response: Response<FundingComment>
            ) {
                if(response.code() == 201 && response.body() != null){
                    responseBody = response.body()!!
                    commentList.add(0, responseBody)
                    viewAdapter.notifyDataSetChanged()
                    comment_form.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Comment Created!",
                        Toast.LENGTH_LONG
                    ).show()
                }else{
                    Toast.makeText(
                        context,
                        "Comment Create Failed",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("writeComment()", response.code().toString())
                }
            }

            override fun onFailure(call: Call<FundingComment>, t: Throwable) {
                Log.e("writeComment()", t.message)
            }
        })
    }
}