package com.k_rona.funding4.data

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Funding(
    var thumbnail_image: String,
    var place: String,
    var title: String,
    var description: String,
    var backedList: ArrayList<String>,
//    var user: String,
    var content_image: String,
    var funding_goal_amount: Int,
    var funding_amount: Int,
    var created_at: Date,
    var ended_at: Date,
    var total_likes: Int
): Serializable