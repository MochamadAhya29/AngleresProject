package com.mochamadahya.angleres.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_update_email.*
import kotlinx.android.synthetic.main.fragment_profile.*

class UpdateEmailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_email)
        setTitle("Ganti Email")

        auth = FirebaseAuth.getInstance()
        val user  = auth.currentUser
        layoutPassword.visibility = View.VISIBLE
        layoutEmail.visibility = View.GONE

        btnAuth.setOnClickListener {
            val password = etPassword.text.toString().trim()
            if (password.isEmpty()){
                etPassword.error = "Password Harus Diisi"
                etPassword.requestFocus()
                return@setOnClickListener
            }
            user?.let {
                val userCredentials = EmailAuthProvider.getCredential(it.email!!, password)
                it.reauthenticate(userCredentials).addOnCompleteListener{
                    if (it.isSuccessful){
                        layoutPassword.visibility = View.GONE
                        layoutEmail.visibility = View.VISIBLE
                    } else if (it.exception is FirebaseAuthInvalidCredentialsException){
                        etPassword.error = "Password Salah"
                        etPassword.requestFocus()
                    } else {
                        Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnUpdateEmail.setOnClickListener {view->
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                etEmail.error = "Email harus diisi"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Email tidak valid"
                etEmail.requestFocus()
                return@setOnClickListener
            }

            user?.let {
                user.updateEmail(email).addOnCompleteListener {
                    if (it.isSuccessful){
                        Intent(this@UpdateEmailActivity, ProfileFragment::class.java).also { intent ->
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}