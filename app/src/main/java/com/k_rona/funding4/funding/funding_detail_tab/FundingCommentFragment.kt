package com.k_rona.funding4.funding.funding_detail_tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k_rona.funding4.R
import com.k_rona.funding4.adapter.BackedListAdapter
import com.k_rona.funding4.adapter.FundingCommentListAdapter
import com.k_rona.funding4.data.FundingComment

class FundingCommentFragment : Fragment() {
    private var commentList: ArrayList<FundingComment> = arrayListOf()

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            commentList = it.getSerializable("funding_comment_list") as ArrayList<FundingComment>
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
        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = FundingCommentListAdapter(commentList, requireContext())

        recyclerView =
            view.findViewById<RecyclerView>(R.id.comment_list_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
    }
}