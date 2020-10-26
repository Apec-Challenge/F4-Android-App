package com.k_rona.funding4.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.k_rona.funding4.R
import com.k_rona.funding4.data.User
import com.k_rona.funding4.network.RetrofitService
import com.k_rona.funding4.network.Server
import com.k_rona.funding4.network.Server.Companion.BASE_URL
import com.k_rona.funding4.utility.LoadingDialog
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd")
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var email: String
        var password: String

        Paper.init(this)

        // 자동 로그인 여부 (User 정보가 저장되어 있으면 자동 로그인 동작)
        val userProfile: User? = Paper.book().read("user_profile")
        if (userProfile != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        login_button.setOnClickListener {
            email = email_edit_text.text.toString()
            password = password_edit_text.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) {
                    email_edit_text.requestFocus()
                    email_edit_text.error = "이메일을 입력해주세요"
                }
                if (password.isEmpty()) {
                    password_edit_text.requestFocus()
                    password_edit_text.error = "비밀번호를 입력해주세요"
                }
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email_edit_text.text.toString()).matches()) {
                email_edit_text.error = "이메일 형식이 올바르지 않습니다"
            } else if (password_edit_text.text.toString().length !in 8..25) {
                password_edit_text.error = "비밀번호는 8자 이상 25자 이하 입니다"
            } else {
                LoadingDialog(this).show()
                requestLogin(email, password)
            }
        }

        reset_password_button.setOnClickListener {

        }

        register_button.setOnClickListener {

        }
    }

    private fun requestLogin(email: String, password: String) {
        retrofitService.requestLogin(email, password).enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                LoadingDialog(applicationContext).dismiss()
                Toast.makeText(
                    applicationContext,
                    "An unexpected error occurred",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                when (response.code()) {
                    400 -> {  // 이메일 및 패스워드 오류
                        Log.e("login", "Password Invalid")
                    }
                    404 -> {  // 미 인증 사용자
                        Log.e("login", "Not activated")
                    }
                    200 -> {  // 로그인 성공
                        LoadingDialog(applicationContext).dismiss()
                        Log.d("login", "Login success")
                        Log.d("login", response.body()!!.email)
                        Log.d("login", response.body()!!.nickname)

                        Paper.book().write("user_profile", response.body())

                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION

                        startActivity(intent)
                        finish()
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingDialog(applicationContext).dismiss()
    }
}