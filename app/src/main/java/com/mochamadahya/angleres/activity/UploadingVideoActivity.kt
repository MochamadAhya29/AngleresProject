package com.mochamadahya.angleres.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.*
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.mochamadahya.angleres.MainActivity
import com.mochamadahya.angleres.R
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_uploading_video.*

class UploadingVideoActivity : AppCompatActivity() {

    companion object {
        private val pick_video = 1
    }

    private val mediaController: MediaController? = null
    private var videoData: Uri? = null
    private var imageData: Uri? = null
    private var myUrl = ""
    private var storagePostPicture: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uploading_video)

        storagePostPicture = FirebaseStorage.getInstance().reference.child("Post Videos")

        setTitle("Upload Video")

        videoview.findViewById<VideoView>(R.id.videoview)
        btnSaveAja.findViewById<Button>(R.id.btnSaveAja)
        progressBar_upload.findViewById<ProgressBar>(R.id.progressBar_upload)
        jdl_post.findViewById<EditText>(R.id.jdl_post)
        desk_post.findViewById<EditText>(R.id.desk_post)

        videoview.setMediaController(mediaController)
        videoview.start()

        btnSaveAja.setOnClickListener {
            uploadImage()
        }

        pickVideoFabUpload.setOnClickListener { chooseVideo() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == pick_video || resultCode == RESULT_OK || data != null || data?.data != null) {
                videoData = data?.data
                videoview.setVideoURI(videoData)
            }
    }

    private fun chooseVideo() {
        val intent = Intent()
        intent.setType("video/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, pick_video)
    }

    private fun uploadImage() {
        when {
            videoData == null -> Toast.makeText(this, "Pilih Video", Toast.LENGTH_SHORT).show()
            TextUtils.isEmpty(jdl_post.text.toString()) -> Toast.makeText(
                this,
                "Tulis Judulnya",
                Toast.LENGTH_SHORT
            ).show()
            TextUtils.isEmpty(desk_post.text.toString()) -> Toast.makeText(
                this,
                "Tulis Deskripsinya",
                Toast.LENGTH_SHORT
            ).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Tambah Postingan")
                progressDialog.setMessage("Tunggu Sebentar...")
                progressDialog.show()

                val fileRef = storagePostPicture!!.child(System.currentTimeMillis().toString() + ".mp4/.3gp")


                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(videoData!!)



                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception.let {
                            throw it!!
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = postRef.push().key

                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["judul"] = jdl_post.text.toString().toLowerCase()
                        postMap["description"] = desk_post.text.toString().toLowerCase()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUrl

                        postRef.child(postId).updateChildren(postMap)

                        Toast.makeText(this, "Posting Berhasil", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, AddVideoBeritaActivity::class.java)
                        startActivity(intent)
                        finish()

                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }
}