package com.mochamadahya.angleres.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mochamadahya.angleres.MainActivity
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_uploading_video.*
import kotlinx.android.synthetic.main.activity_video_stream_player.*
import java.lang.System.load


class DetailActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser? = null
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = firebaseDatabase.reference
    private val childReference: DatabaseReference = databaseReference.child("postimage")
    private val uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setTitle("Postingan")
//        userUid = FirebaseAuth.getInstance().currentUser?.uid
        tv_title_detail.setText(intent.getStringExtra("judul"))
        tv_description_detail.setText(intent.getStringExtra("description"))
        txt_like_detail.setText(intent.getStringExtra("like"))

//        img_like_detail.setOnClickListener {
//            if (img_like_detail.tag == "Like") {
//                FirebaseDatabase.getInstance().reference
//                    .child("Likes").child(post.getPostId()).child(firebaseUser!!.uid)
//                    .setValue(true)
//            } else {
//                FirebaseDatabase.getInstance().reference
//                    .child("Likes").child(post.getPostId()).child(firebaseUser!!.uid)
//                    .removeValue()
//
//                val intent = Intent(mContext, MainActivity::class.java)
//                mContext.startActivity(intent)
//            }
//
//        Picasso.get().load(intent.getStringExtra("postimage")).into(img_detail)

        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(intent.getStringExtra("postid")!!)
        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    txt_like_detail.text = "${p0.childrenCount.toString()}  Likes"
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
//
//
        })

        video_detail.findViewById<VideoView>(R.id.video_detail)
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Tunggu Sebentar...")
        progressDialog.show()

        val videoRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(intent.getStringExtra("postimage")!!)
        videoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    video_detail.setVideoURI(uri)
                    video_detail.start()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
//
//
        })



    }

}