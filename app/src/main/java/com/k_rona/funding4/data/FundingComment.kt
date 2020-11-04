package com.k_rona.funding4.data

import java.io.Serializable
import java.util.*

class FundingComment(
    var id: Int,
    var username: String,
    var funding: Int,
    var content: String,
    var created_at: Date
): Serializable