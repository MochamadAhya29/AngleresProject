package com.mochamadahya.angleres.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mochamadahya.angleres.MainActivity
import com.mochamadahya.angleres.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()


        btn_forgotPassword.setOnClickListener {
            Intent(this@LoginActivity, ResetPasswordActivity::class.java).also {
                startActivity(it)
            }
        }
        btnLogin.setOnClickListener {
            val email = etEmailLogin.text.toString().trim()
            val password = etPasswordLogin.text.toString().trim()

            if (email.isEmpty()) {
                etEmailLogin.error = "Email harus diisi"
                etEmailLogin.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmailLogin.error = "Email tidak valid"
                etEmailLogin.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 2) {
                etPasswordLogin.error = "Password harus lebih dari 6 Karakter"
                etPasswordLogin.requestFocus()
                return@setOnClickListener
            }
            loginUser(email, password)
        }
        txtRegister.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null) {
            Intent(this@LoginActivity, MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

    }
}