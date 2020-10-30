package com.k_rona.funding4.data

import java.util.*

class Funding(
    var thumbnail_image: String,
    var place: String,
    var title: String,
    var description: String,
    var user: String,
    var content_image: String,
    var funding_goal_amount: Int,
    var funding_amount: Int,
    var like_count: String,
    var created_at: Date,
    var ended_at: Date
)