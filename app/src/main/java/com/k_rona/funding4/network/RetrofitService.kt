package com.k_rona.funding4.network

import com.k_rona.funding4.data.Funding
import com.k_rona.funding4.data.LodgingPlace
import com.k_rona.funding4.data.Review
import com.k_rona.funding4.data.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    /** Account **/

    @FormUrlEncoded
    @POST("rest-auth/registration/")
    fun requestRegister(
        @Field("email") email: String,
        @Field("password1") password1: String,
        @Field("password2") password2: String
    ): Call<String>

    @FormUrlEncoded
    @POST("rest-auth/login/")
    fun requestLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<User>

    @FormUrlEncoded
    @POST("rest-auth/password/change/")
    fun requestChangePassword(
        @Field("old_password") oldPassword: String,
        @Field("new_password1") newPassword1: String,
        @Field("new_password2") newPassword2: String
    ): Call<String>

    @FormUrlEncoded
    @POST("rest-auth/password/reset/")
    fun requestResetPassword(
        @Field("email") email: String
    ): Call<String>

    /** Place **/

    @GET("api/place/")
    fun requestSurroundPlaceList(): Call<ArrayList<LodgingPlace>>

    @GET("api/place/{place_id}")
    fun requestFundingPlace(
        @Path("place_id") placeID: String
    ): Call<LodgingPlace>

    @GET("api/place/")
    fun requestPopularPlaceList(
        @Query("q") filter: String
    ): Call<ArrayList<LodgingPlace>>

    @Multipart
    @POST("api/place/")
    fun registerPlace(
        @Part("place_id") placeID: RequestBody,
        @Part("title") placeTitle: RequestBody,
        @Part place_image: MultipartBody.Part,
        @Part("description") placeDescription: RequestBody,
        @Part("address") placeAddress: RequestBody,
        @Part("lng") placeLongitude: RequestBody,
        @Part("lat") placeLatitude: RequestBody,
        @Part("person_hygiene") personHygiene: RequestBody,
        @Part("body_temperature_check") temperatureCheck: RequestBody,
        @Part("hand_sanitizer") handSanitizer: RequestBody
    ): Call<LodgingPlace>

    /** Funding **/

    @GET("api/funding/")
    fun requestFundingList(@Query("q") filter: String): Call<ArrayList<Funding>>
//        @Query("keyword") keyword: String,

    @GET("api/funding/")
    fun requestPopularFundingList(@Query("q") filter: String): Call<ArrayList<Funding>>

    /** Review **/

    @GET("api/review/")
    fun requestReviewList(
        @Query("place") placeID: String
    ): Call<ArrayList<Review>>

    @FormUrlEncoded
    @POST("api/review/")
    fun requestPostReview(
        @Field("user") nickname: String,
        @Field("place") placeID: String,
        @Field("content") content: String,
        @Field("rating") rating: Float
    ): Call<Review>
}