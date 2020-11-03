package com.k_rona.funding4.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.k_rona.funding4.R
import com.k_rona.funding4.data.FundingComment
import com.k_rona.funding4.data.User
import io.paperdb.Paper
import kotlinx.android.synthetic.main.funding_comment_item.view.*
import kotlinx.android.synthetic.main.place_review_item.view.*
import java.text.SimpleDateFormat

class FundingCommentListAdapter(
    private val commentList: ArrayList<FundingComment>,
    private val context: Context
) : RecyclerView.Adapter<FundingCommentListAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FundingCommentListAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.funding_comment_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FundingCommentListAdapter.ViewHolder, position: Int) {

        val commentDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val commentCreatedAt = commentDateFormat.format(commentList[position].created_at)

        Paper.init(context)
        val userProfile: User? = Paper.book().read("user_profile")

        holder.view.comment_created_at.text = commentCreatedAt
        holder.view.comment_content.text = commentList[position].content
        holder.view.comment_writer.text = commentList[position].user
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

}