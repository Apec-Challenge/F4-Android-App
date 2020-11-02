package com.k_rona.funding4.user

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
import com.k_rona.funding4.home.HomeActivity
import com.k_rona.funding4.network.RetrofitService
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
                    email_edit_text.error = "Input your E-mail Address"
                }
                if (password.isEmpty()) {
                    password_edit_text.requestFocus()
                    password_edit_text.error = "Input your Password"
                }
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email_edit_text.text.toString()).matches()) {
                email_edit_text.error = "E-mail Address format is invalid"
            } else if (password_edit_text.text.toString().length !in 8..25) {
                password_edit_text.error = "The password is from 8 to 25 characters"
            } else {
                requestLogin(email, password)
            }
        }

        reset_password_button.setOnClickListener {
            startActivity(Intent(this, PasswordResetActivity::class.java))
        }

        register_button.setOnClickListener {
            LoadingDialog(applicationContext).show()
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun requestLogin(email: String, password: String) {
        retrofitService.requestLogin(email, password).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                LoadingDialog(applicationContext).dismiss()
                when (response.code()) {
                    400 -> {  // 이메일 및 패스워드 오류
                        Toast.makeText(applicationContext, "Check your E-mail or password", Toast.LENGTH_LONG).show()
                        Log.e("login", "Password Invalid")
                        Toast.makeText(
                            applicationContext,
                            "Email and password are not valid",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    404 -> {  // 미 인증 사용자
                        Toast.makeText(applicationContext, "Please check your e-mail box!", Toast.LENGTH_LONG).show()
                        Log.e("login", "Not activated")
                        Toast.makeText(
                            applicationContext,
                            "Unauthenticated user. Please complete email verification.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    200 -> {  // 로그인 성공
                        Log.d("login", "Login success")
                        Log.d("login", response.body()!!.email)
                        Log.d("login", response.body()!!.nickname)

                        Toast.makeText(applicationContext, "Welcome " + response.body()!!.nickname, Toast.LENGTH_LONG).show()

                        Paper.book().write("user_profile", response.body())

                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_LONG).show()
                        Log.d("login", "Login failed (Server Error)")
                        Log.d("login", response.code().toString())
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                LoadingDialog(applicationContext).dismiss()
                Toast.makeText(
                    applicationContext,
                    "An unexpected error occurred",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        LoadingDialog(applicationContext).dismiss()
    }
}