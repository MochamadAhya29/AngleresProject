package com.mochamadahya.angleres.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.mochamadahya.angleres.MainActivity
import com.mochamadahya.angleres.R
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener {
            val nickname = etNickNameregister.text.toString().trim()
            val email = etEmailRegister.text.toString().trim()
            val password = etPasswordRegister.text.toString().trim()
            val repassword = etRePasswordRegister.text.toString().trim()
            val numbercontact = etNumberContactRegister.text.toString().trim()
            val address = etAddressRegister.text.toString().trim()

            if (nickname.isEmpty()){
                etNickNameregister.error = "Nama Harus Diisi"
                etNickNameregister.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()){
                etEmailRegister.error = "Email harus diisi"
                etEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                etEmailRegister.error = "Email tidak valid"
                etEmailRegister.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 2 ){
                etPasswordRegister.error = "Password harus lebih dari 6 Karakter"
                etPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (repassword.isEmpty() || repassword.length < 2 ){
                etPasswordRegister.error = "Password harus lebih dari 6 Karakter"
                etPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            if (numbercontact.isEmpty()){
                etNumberContactRegister.error = "Nomor Telepon Harus Diisi"
                etNumberContactRegister.requestFocus()
                return@setOnClickListener
            }

            if (address.isEmpty()){
                etAddressRegister.error = "Alamat Harus Diisi"
                etAddressRegister.requestFocus()
                return@setOnClickListener
            }

            registerUser(email, password)
        }

        txtLogin.setOnClickListener {
            Intent(this@RegisterActivity, LoginActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Intent(this@RegisterActivity, MainActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }


//    override fun onStart() {
//        super.onStart()
//        Intent(this@RegisterActivity, MainActivity::class.java).also {
//            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(it)
//        }
//    }
    }
}