package com.k_rona.funding4.data

import java.io.Serializable

class LodgingPlace(
    var place_id: String,
    var title: String,
    var place_image: String,
    var description: String,
    var address: String,
    var user_likes: ArrayList<Int>,
    var lng: String,
    var lat: String,
    var total_likes: Int = 0,
    var hand_sanitizer: Int,
    var person_hygiene: Int,
    var body_temperature_check: Int,
    var review_average: Float
): Serializable