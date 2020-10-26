package com.k_rona.funding4.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import kotlinx.android.synthetic.main.activity_password_reset.*
import kotlinx.android.synthetic.main.activity_password_reset.email_edit_text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PasswordResetActivity : AppCompatActivity() {

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Server.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        var email: String

        send_button.setOnClickListener {
            email = email_edit_text.text.toString()

            if (email.isEmpty()) {
                email_edit_text.requestFocus()
                email_edit_text.error = "Input your E-mail Address"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email_edit_text.text.toString()).matches()) {
                email_edit_text.requestFocus()
                email_edit_text.error = "E-mail Address format is invalid"
            } else {
                requestPasswordReset(email)
            }
        }
    }

    private fun requestPasswordReset(email: String) {
        retrofitService.requestResetPassword(email).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                when (response.code()) {
                    400 -> {
                    }
                    404 -> {
                    }
                    200 -> {  // 비밀번호 초기화 링크 전송 성공
                        val intent = Intent(applicationContext, PasswordResetActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION

                        startActivity(intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Password Reset", t.message)
            }
        })
    }
}