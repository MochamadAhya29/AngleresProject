package com.mochamadahya.angleres.activity.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.fragment.ProfileFragment
import kotlinx.android.synthetic.main.activity_change_password.*

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setTitle("Ganti Password")

        auth = FirebaseAuth.getInstance()
        val user  = auth.currentUser
        layoutPassword.visibility = View.VISIBLE
        layoutNewPassword.visibility = View.GONE

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
                        layoutNewPassword.visibility = View.VISIBLE
                    } else if (it.exception is FirebaseAuthInvalidCredentialsException){
                        etEmailReset.error = "Password Salah"
                        etEmailReset.requestFocus()
                    } else {
                        Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnUpdatePassword.setOnClickListener {view->
            val newPassword = etEmailReset.text.toString().trim()
            val newPasswordConfirm = etNewPasswordConfirm.text.toString().trim()

            if (newPassword.isEmpty() || newPassword.length < 3) {
                etEmailReset.error = "Password Harus Lebih dari 3 Karakter"
                etEmailReset.requestFocus()
                return@setOnClickListener
            }

            if (newPassword != newPasswordConfirm){
                etNewPasswordConfirm.error = "Password tidak sama"
                etNewPasswordConfirm.requestFocus()
                return@setOnClickListener
            }

            user?.let {
                user.updatePassword(newPassword).addOnCompleteListener {
                    if (it.isSuccessful){
                        Intent(this@ChangePasswordActivity, ProfileFragment::class.java).also { intent ->
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