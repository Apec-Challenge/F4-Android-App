package com.k_rona.funding4.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.k_rona.funding4.R
import kotlinx.android.synthetic.main.activity_password_reset_confirm.*

class PasswordResetConfirmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset_confirm)

        confirm_button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}