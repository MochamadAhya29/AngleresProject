package com.mochamadahya.angleres.videostreamer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mochamadahya.angleres.R

import kotlinx.android.synthetic.main.video_related_video.view.*

class VideoRelatedAdapter(val context: Context, val videoList: ArrayList<VideoListModel>):
    RecyclerView.Adapter<VideoRelatedAdapter.ViewHolder>() {

    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listItem : VideoListModel, firebaseUser : FirebaseUser?){
            val tvTitle = itemView.tv_title
            val tvDescription = itemView.tv_description
            val ivThumbnail = itemView.iv_thumbnail
            val clVideo = itemView.cl_video
            //val likeButton = itemView.img_likes


            if(listItem.id != null && firebaseUser != null){
                //isLikes(listItem.id, firebaseUser)

//                likeButton?.setOnClickListener {
//                    if (likeButton.tag == "Like"){
//                        FirebaseDatabase.getInstance().reference
//                            .child("Likes").child(listItem.id).child(firebaseUser.uid)
//                            .setValue(true)
//                    }
//                    else {
//                        FirebaseDatabase.getInstance().reference
//                            .child("Like").child(listItem.id).child(firebaseUser.uid)
//                            .removeValue()
//                    }
//                }
                tvTitle.text = listItem.title
                tvDescription.text = listItem.description
                Glide.with(itemView.context).load(listItem.thumb).into(ivThumbnail)
                val stLog = "title: ${listItem.title}, des: ${listItem.description}, img: ${listItem.thumb}"
                Log.d("ViRelaAd", stLog)
            }
        }

//        private fun isLikes(id: String?,  firebaseUser: FirebaseUser) {
//            if(itemView.img_likes == null) Log.d("hai", "null") else Log.d("hai", "gak null")
//
//            val likesRef = FirebaseDatabase.getInstance().reference
//                .child("Likes").child(id!!)
//
//            if(itemView.img_likes == null) Log.d("hai", "apa null") else Log.d("hai", "apa gak null")
//
//            likesRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if(itemView.img_likes == null) Log.d("hai", "ini null") else Log.d("hai", "ini gak null")
//                    if (snapshot.child(firebaseUser.uid).exists()){
//                        itemView.img_likes.setBackgroundResource(R.drawable.ic_likee)
//                        itemView.img_likes.tag= "Liked"
//                    }
//                    else {
//                        itemView.img_likes.setBackgroundResource(R.drawable.ic_like)
//                        itemView.img_likes.tag = "Like"
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//            })
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.video_related_video,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = videoList.get(position)
        holder.bind(listItem, firebaseUser)
    }
}