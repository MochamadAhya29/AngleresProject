package com.mochamadahya.angleres.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mochamadahya.angleres.R
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        setTitle("Lupa Password")

        btnReset.setOnClickListener {
            val email  = etEmailReset.text.toString().trim()

            if (email.isEmpty()) {
                etEmailReset.error = "Email harus diisi"
                etEmailReset.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmailReset.error = "Email tidak valid"
                etEmailReset.requestFocus()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Cek Email untuk Reset Password", Toast.LENGTH_SHORT).show()
                    Intent(this@ResetPasswordActivity, LoginActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}