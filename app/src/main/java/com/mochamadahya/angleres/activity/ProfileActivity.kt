package com.mochamadahya.angleres.activity

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.activity.auth.ChangePasswordActivity
import com.mochamadahya.angleres.activity.auth.LoginActivity
import com.mochamadahya.angleres.activity.auth.UpdateEmailActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProfileActivity : AppCompatActivity() {

    companion object{
        const val REQUEST_CAMERA = 100
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "AngleresImages"
        private const val PLACE_AUTOCOMPLETE_REQUEST_CODE = 3
    }



    private lateinit var imageUri : Uri
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setTitle("Profil")

        auth = FirebaseAuth.getInstance()
        val user  = auth.currentUser

        val ivProfile: CircleImageView = findViewById(R.id.ivProfile)


        btnKeluar.setOnClickListener {
            auth.signOut()
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        btnUpdate.setOnClickListener {
            val image = when{
                ::imageUri.isInitialized -> imageUri
                user?.photoUrl == null -> Uri.parse("https://media.defense.gov/2020/Feb/19/2002251686/700/465/0/200219-A-QY194-002.JPG")
                else -> user.photoUrl
            }

            val name  = etName.text.toString().trim()
            if (name.isEmpty()){
                etName.error = "Nama Harus diisi"
                etName.requestFocus()
                return@setOnClickListener
            }

            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this, "Sudah di perbaharui", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        img_unverified.setOnClickListener {
            user?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Email verifikasi telah dikirim ", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        txtmintaverifikasi.setOnClickListener {
            user?.sendEmailVerification()?.addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this, "Email verifikasi telah dikirim ", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        txtChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        etEmail.setOnClickListener {
            startActivity(Intent(this, UpdateEmailActivity::class.java))
        }

        if (user !=null){
            if (user.photoUrl != null){
                Picasso.get().load(user.photoUrl).into(ivProfile)
            } else {
                Picasso.get().load("https://media.defense.gov/2020/Feb/19/2002251686/700/465/0/200219-A-QY194-002.JPG").into(ivProfile)
            }
            etName.setText(user.displayName)
            etEmail.setText(user.email)

            if (user.isEmailVerified){
                img_verified.visibility = View.VISIBLE
                txtmintaverifikasi.visibility = View.GONE
                txtmintaverifikasi2.visibility = View.GONE
            } else {
                img_unverified.visibility = View.VISIBLE
                txtmintaverifikasi.visibility = View.VISIBLE
                txtmintaverifikasi2.visibility = View.VISIBLE
            }

            if (user.phoneNumber.isNullOrEmpty()){
                etPhone.setText("Masukkan Nomor Telepon")
            } else {
                etPhone.setText(user.phoneNumber)
            }
        }

        ivProfile.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf(
                "Select Photo From Galery",
                "Capture photo from camera"
            )
            pictureDialog.setItems(pictureDialogItems) { _, which ->
                when (which) {
                    0 -> choosePhotoFromGalery()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
    }

    private fun takePhotoFromCamera() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(
                report:
                MultiplePermissionsReport
            ) {
                if (report!!.areAllPermissionsGranted()) {
                }
                val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(galleryIntent, CAMERA)
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermssions()
            }
        }).onSameThread().check()
    }

    private fun choosePhotoFromGalery() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent, GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogForPermssions()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialogForPermssions() {
        AlertDialog.Builder(this).setMessage(
            "" +
                    "It Looks like you have turned off permission required" +
                    "for this feature. It can be enabled under the" +
                    "Applications Settings"
        )
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("CANCEL") { dialog, which ->
                dialog.dismiss()
            }.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentUri = data.data
                    try {
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)

                        uploadImage(selectedImageBitmap)
                        ivProfile.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                }

            }
            else if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
                val imgBitmap = data?.extras?.get("data") as Bitmap
                uploadImage(imgBitmap)

            }

        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
    }

//    private fun saveImageToInternalStorage(bitmap: Bitmap?): Uri? {
//        val wrapper = ContextWrapper(context)
//        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
//        file = File(file, "${UUID.randomUUID()}.jpg")
//
//        try {
//            val stream: OutputStream = FileOutputStream(file)
//            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//            stream.flush()
//            stream.close()
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        return Uri.parse(file.absolutePath)
//    }

    //    private fun intentCamera() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent->
//            intent.resolveActivity(this.packageManager).also {
//                startActivityForResult(intent, REQUEST_CAMERA)
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK){
//            val imgBitmap = data?.extras?.get("data") as Bitmap
//            uploadImage(imgBitmap)
//        }
//    }
//
    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref = FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")

        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        ref.putBytes(image)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    ref.downloadUrl.addOnCompleteListener{
                        it.result?.let {
                            imageUri = it
                            ivProfile?.setImageBitmap(imgBitmap)
                        }
                    }
                }
            }

    }
}