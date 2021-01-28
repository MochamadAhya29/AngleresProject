package com.mochamadahya.angleres.fragment

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.activity.ProfileActivity
import com.mochamadahya.angleres.activity.auth.ChangePasswordActivity
import com.mochamadahya.angleres.activity.auth.LoginActivity
import com.mochamadahya.angleres.activity.auth.ResetPasswordActivity
import com.mochamadahya.angleres.activity.auth.UpdateEmailActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import java.io.IOException


class ProfileFragment : Fragment() {



    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val user  = auth.currentUser


        Glide.with(view)
            .load(user?.photoUrl)
            .into(img_profil)

        txtUserProfil.setText(user?.displayName)
        txtEmailProfil.setText(user?.email)


        linearLayoutProfile.setOnClickListener {
            startActivity(Intent(context, ProfileActivity::class.java))
        }
//        txt_ubah_email.setOnClickListener {
//            startActivity(Intent(context, UpdateEmailActivity::class.java))
//        }
//        txt_ubah_password.setOnClickListener {
//            startActivity(Intent(context, ResetPasswordActivity::class.java))
//        }

    }

}