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
import com.k_rona.funding4.adapter.ReviewListAdapter
import java.util.ArrayList

class FundingBackedListFragment : Fragment() {
    private var backedList: ArrayList<String> = arrayListOf()

    lateinit var recyclerView: RecyclerView
    lateinit var viewAdapter: RecyclerView.Adapter<*>
    lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            backedList = it.getStringArrayList("funding_backed_list")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_funding_backed_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = BackedListAdapter(backedList, requireContext())

        recyclerView =
            view.findViewById<RecyclerView>(R.id.backed_list_recyclerview).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
    }
}