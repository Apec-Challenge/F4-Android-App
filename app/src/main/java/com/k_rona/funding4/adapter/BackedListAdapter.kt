package com.k_rona.funding4.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.k_rona.funding4.R
import kotlinx.android.synthetic.main.backed_listitem.view.*

class BackedListAdapter(
    private val backedList: ArrayList<String>,
    private val context: Context
) : RecyclerView.Adapter<BackedListAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BackedListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.backed_listitem, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BackedListAdapter.ViewHolder, position: Int) {
        holder.view.backed_nickname.text = backedList[position]
    }

    override fun getItemCount(): Int {
        return backedList.size
    }
}