package com.k_rona.funding4.data

class UserInfo (
    var id: Int,
    var email: String,
    var nickname: String,
    var money: Int,
    var place_likes: ArrayList<String>,
    var backed_list: ArrayList<BackedFunding>
)