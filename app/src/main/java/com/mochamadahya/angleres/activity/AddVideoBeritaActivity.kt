package com.mochamadahya.angleres.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.mochamadahya.angleres.MainActivity
import com.mochamadahya.angleres.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_video_berita.*
import kotlinx.android.synthetic.main.activity_add_video_berita.progressBar_upload
import kotlinx.android.synthetic.main.activity_uploading_video.*

class AddVideoBeritaActivity : AppCompatActivity() {

//    private var myUrl = ""
//    private var imageUrl : Uri? = null
//    private var storagePostPicture: StorageReference? = null
//
//    private var VIDEO : Int = 3
//    private var uri : Uri? = null
//    private var mStorage : StorageReference? =null


    private val VIDEO_PICK_GALLERY_CODE = 100
    private val VIDEO_PICK_CAMERA_CODE = 101
    private val CAMERA_REQUEST_CODE = 102
    private lateinit var actionBar: ActionBar
    private lateinit var cameraPermissions: Array<String>
    private var videoUri: Uri? = null
    private var storageReference: StorageReference? = null
    private lateinit var progressDialog: ProgressDialog
    private var title: String = ""
    private var description: String = ""
    private val mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video_berita)

        val storageReference = FirebaseStorage.getInstance().getReference("Post Videos")
//        storagePostPicture = FirebaseStorage.getInstance().reference.child("Post Picture")
//        btnClosePost.setOnClickListener {
//            onBackPressed()
//        }

//        btnSavePost.setOnClickListener { uploadImage() }
        setTitle("Tambah Postingan")

//
//        CropImage.activity()
//            .setAspectRatio(2, 1)
//            .start(this)

        video_post.findViewById<VideoView>(R.id.video_post)
        btnSavePost.findViewById<Button>(R.id.btnSavePost)
//        progressBar_upload.findViewById<ProgressBar>(R.id.progressBar_upload)
        judul_post.findViewById<EditText>(R.id.judul_post)
        deskripsi_post.findViewById<EditText>(R.id.deskripsi_post)

        actionBar = supportActionBar!!
        actionBar.title = "Tambah Video Baru"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        cameraPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Mohon Tunggu")
        progressDialog.setMessage("Upload Video....")
        progressDialog.setCanceledOnTouchOutside(false)

        video_post.setMediaController(mediaController)
        video_post.start()

        pickVideoFab?.setOnClickListener {
            videoPickDialog()
        }

        btnSavePost.setOnClickListener {
            title = judul_post.text.toString().trim()
            description = deskripsi_post.text.toString().trim()

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Judul Wajib", Toast.LENGTH_SHORT).show()
            } else if (videoUri == null) {
                Toast.makeText(this, "Ambil Video Dulu", Toast.LENGTH_SHORT).show()
            } else {
                uploadVideoFirebase()
            }
        }

    }

    private fun setVideoToVideoView() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(video_post)
        video_post.setMediaController(mediaController)
        video_post.setVideoURI(videoUri)

        video_post.requestFocus()
        video_post.setOnPreparedListener {
            video_post.pause()
        }
    }


    private fun uploadVideoFirebase() {
        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()
        val filePathAndName = "video/video_$timestamp"

        storageReference!!.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result
                if (uriTask.isSuccessful) {
//                    val hashmap = HashMap<String, Any>()
//                    hashmap["id"] = "$timestamp"
//                    hashmap["title"] = "$title"
//                    hashmap["description"] = "$description"
//                    hashmap["timestamp"] = "$timestamp"
//                    hashmap["videoUri"] = "$downloadUri"

                    val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                    val postId = postRef.push().key

                    val postMap = HashMap<String, Any>()
                    postMap["postid"] = postId!!
                    postMap["judul"] = judul_post.text.toString().toLowerCase()
                    postMap["description"] = deskripsi_post.text.toString().toLowerCase()
                    postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                    postMap["postimage"] = "$downloadUri"

                    postRef.child(postId).updateChildren(postMap)

                    Toast.makeText(this, "Posting Berhasil", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                    progressDialog.dismiss()
                } else {
                    progressDialog.dismiss()

                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun videoPickDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ambil Video Dari")
            .setItems(options) { dialogInterface, i ->
                if (i == 0) {
                    if (!checkCameraPermissions()) {
                        requestCameraPermissions()
                    } else {
                        videoPickCamera()
                    }
                } else {
                    videoPickGallery()
                }
            }
            .show()
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermissions(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    private fun videoPickGallery() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Pilih Video"),
            VIDEO_PICK_GALLERY_CODE
        )
    }

    private fun videoPickCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            CAMERA_REQUEST_CODE ->
                if (grantResults.size > 0) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        videoPickCamera()
                    } else {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == VIDEO_PICK_CAMERA_CODE) {
                videoUri == data!!.data
                setVideoToVideoView()
            } else if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                videoUri == data!!.data
                setVideoToVideoView()
            }
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            setVideoToVideoView()
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
//            val result = CropImage.getActivityResult(data)
//            imageUrl = result.uri
//            image_post.setImageURI(imageUrl)
//        }
//    }
//
//    private fun uploadImage() {
//        when{
//            imageUrl == null -> Toast.makeText(this, "Pilih Gambar", Toast.LENGTH_SHORT).show()
//            TextUtils.isEmpty(judul_post.text.toString()) -> Toast.makeText(this, "Tulis Judulnya", Toast.LENGTH_SHORT).show()
//            TextUtils.isEmpty(deskripsi_post.text.toString()) -> Toast.makeText(this, "Tulis Deskripsinya", Toast.LENGTH_SHORT).show()
//
//            else -> {
//                val progressDialog = ProgressDialog(this@AddVideoBeritaActivity)
//                progressDialog.setTitle("Tambah Postingan")
//                progressDialog.setMessage("Tunggu Sebentar...")
//                progressDialog.show()
//
//                val fileRef = storagePostPicture!!.child(System.currentTimeMillis().toString() + ".jpg")
//
//                val uploadTask: StorageTask<*>
//                uploadTask = fileRef.putFile(imageUrl!!)
//
//                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
//                    if (!task.isSuccessful){
//                        task.exception.let {
//                            throw it!!
//                            progressDialog.dismiss()
//                        }
//                    }
//                    return@Continuation fileRef.downloadUrl
//                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
//                    if (task.isSuccessful){
//                        val downloadUrl = task.result
//                        myUrl = downloadUrl.toString()
//
//                        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
//                        val postId = postRef.push().key
//
//                        val postMap = HashMap<String, Any>()
//                        postMap["postid"]      = postId!!
//                        postMap["judul"]       = judul_post.text.toString().toLowerCase()
//                        postMap["description"] = deskripsi_post.text.toString().toLowerCase()
//                        postMap["publisher"]   = FirebaseAuth.getInstance().currentUser!!.uid
//                        postMap["postimage"]   = myUrl
//
//                        postRef.child(postId).updateChildren(postMap)
//
//                        Toast.makeText(this, "Posting Berhasil",  Toast.LENGTH_SHORT).show()
//
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
//
//                        progressDialog.dismiss()
//                    }else{
//                        progressDialog.dismiss()
//                    }
//                })
//            }
//        }
//    }
}