package com.mochamadahya.angleres.activity

import android.Manifest
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
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mochamadahya.angleres.R
import kotlinx.android.synthetic.main.activity_add_video_berita.*

class AddVideoBeritaActivity : AppCompatActivity() {

    private  val VIDEO_PICK_GALLERY_CODE = 100
    private  val VIDEO_PICK_CAMERA_CODE = 101
    private  val CAMERA_REQUEST_CODE = 102

    private lateinit var actionBar: ActionBar

    private lateinit var cameraPermissions:Array<String>


    private var videoUri: Uri? = null

    private lateinit var progressDialog: ProgressDialog

    private var title: String = ""
    private var description: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_video_berita)

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

        var videoUri2: Uri? = videoUri

        pickVideoFab?.setOnClickListener {
            videoPickDialog()
        }

        uploadVideoBtn.setOnClickListener {
            title = titleEt.text.toString().trim()
            description = descEt.text.toString().trim()

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Judul Wajib", Toast.LENGTH_SHORT).show()
            } else if (videoUri2== null) {
                Toast.makeText(this, "Ambil Video Dulu", Toast.LENGTH_SHORT).show()
            } else{
                uploadVideoFirebase()
            }
        }


    }

    private fun setVideoToVideoView() {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)

        videoView.requestFocus()
        videoView.setOnPreparedListener {
            videoView.pause()
        }
    }


    private fun uploadVideoFirebase() {
        progressDialog.show()

        val timestamp = ""+System.currentTimeMillis()
        val filePathAndName = "video/video_$timestamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(videoUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val downloadUri = uriTask.result
                if (uriTask.isSuccessful) {
                    val hashmap = HashMap<String, Any>()
                    hashmap["id"] = "$timestamp"
                    hashmap["title"] = "$title"
                    hashmap["description"] = "$description"
                    hashmap["timestamp"] = "$timestamp"
                    hashmap["videoUri"] = "$downloadUri"

                    val dbReference = FirebaseDatabase.getInstance().getReference("video")
                    dbReference.child(timestamp)
                        .setValue(hashmap)
                        .addOnSuccessListener { taskSnapshot ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "Video sudah di Upload", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun videoPickDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ambil Video Dari")
            .setItems(options) { dialogInterface, i->
                if (i==0) {
                    if (!checkCameraPermissions()) {
                        requestCameraPermissions()
                    }
                    else {
                        videoPickCamera()
                    }
                }
                else {
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
}