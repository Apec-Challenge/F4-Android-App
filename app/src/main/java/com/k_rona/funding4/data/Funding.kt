package com.k_rona.funding4.data

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Funding(
    var id: Int,
    var thumbnail_image: String,
    var place: LodgingPlace,
    var title: String,
    var description: String,
    var owner_username: String,
    var backed_list: ArrayList<String>,
    var content_image: String,
    var content_text: String,
    var funding_goal_amount: Int,
    var funding_amount: Int,
    var created_at: Date,
    var ended_at: Date,
    var total_likes: Int,
    var user_likes: ArrayList<Int>,
    var comment_list: ArrayList<FundingComment>
) : Serializable