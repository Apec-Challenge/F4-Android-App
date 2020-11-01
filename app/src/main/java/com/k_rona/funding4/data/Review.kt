package com.k_rona.funding4.data

import java.util.*
import kotlin.collections.ArrayList

class Review(
    var id: Int,
    var user: Int,
    var place: String,
    var content: String,
    var user_likes: ArrayList<Int>,
    var created_at: Date,
    var updated_at: Date,
    var rating: Float,
    var total_likes: Int
)