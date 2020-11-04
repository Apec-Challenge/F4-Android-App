package com.k_rona.funding4.network

import com.k_rona.funding4.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
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

    @GET("place_like/{nickname}/{id}/")
    fun requestPlaceLikeButtonPushed(
        @Path("nickname") nickname: String,
        @Path("id") placeID: String
    ): Call<Void>

    /** Funding **/

    @GET("api/funding/")
    fun requestFundingList(
        @Query("q") filter: String,
        @Query("title__icontains") titleKeyword: String,
        @Query("description__icontains") descriptionKeyword: String,
        @Query("content_text__icontains") contentKeyword: String
    ): Call<ArrayList<Funding>>

    @GET("api/funding/")
    fun requestPopularFundingList(@Query("q") filter: String): Call<ArrayList<Funding>>

    @GET("funding_like/{nickname}/{id}")
    fun requestFundingLikeButtonPushed(
        @Path("nickname") nickname: String,
        @Path("id") fundingID: Int
    ): Call<Void>

    @FormUrlEncoded
    @POST("api/funding-comment/")
    fun requestPostComment(
        @Field("username") nickname: String,
        @Field("funding") fundingID: Int,
        @Field("content") content: String
    ):Call<FundingComment>

    @GET("api/main-funding/")
    fun requestMainFundingList(): Call<ArrayList<MainFunding>>

    /** Review **/

    @GET("api/review/")
    fun requestReviewList(
        @Query("place") placeID: String
    ): Call<ArrayList<Review>>

    @FormUrlEncoded
    @POST("api/review/")
    fun requestPostReview(
        @Field("user") userID: Int,
        @Field("place") placeID: String,
        @Field("content") content: String,
        @Field("rating") rating: Int
    ): Call<Review>

    @GET("review_like/{nickname}/{id}/")
    fun requestReviewLikeButtonPushed(
        @Path("nickname") nickname: String,
        @Path("id") reviewID: Int
    ): Call<Void>
}