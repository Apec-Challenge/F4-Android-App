package com.k_rona.funding4.data

import java.io.Serializable

class RegisterPlace(
    var place_id: String,
    var title: String,
    var place_image: String,
    var description: String,
    var address: String,
    var lng: String,
    var lat: String,
    var hand_sanitizer: Int,
    var person_hygiene: Int,
    var body_temperature_check: Int
): Serializable