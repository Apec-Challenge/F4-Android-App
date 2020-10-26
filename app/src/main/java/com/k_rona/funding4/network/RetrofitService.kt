package com.k_rona.funding4.network

import com.k_rona.funding4.data.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RetrofitService {

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
}