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
import kotlinx.android.synthetic.main.activity_video_stream_player.view.*
import kotlinx.android.synthetic.main.activity_video_stream_player.view.tv_description
import kotlinx.android.synthetic.main.activity_video_stream_player.view.tv_title
import kotlinx.android.synthetic.main.video_related_video.view.*

class VideoRelatedAdapter(val context: Context, val videoList: ArrayList<VideoListModel>):
    RecyclerView.Adapter<VideoRelatedAdapter.ViewHolder>() {

    private val firebaseUser: FirebaseUser? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view.tv_title
        val tvDescription = view.tv_description
        val ivThumbnail = view.iv_thumbnail
        val clVideo = view.cl_video
        var likeButton =view.img_like
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
        isLikes(listItem.id!!, holder.likeButton!!)

        holder.likeButton?.setOnClickListener {
            if (holder.likeButton.tag == "Like"){
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(listItem.id).child(firebaseUser!!.uid)
                    .setValue(true)
            }
            else {
                FirebaseDatabase.getInstance().reference
                    .child("Like").child(listItem.id).child(firebaseUser!!.uid)
                    .removeValue()
            }
        }
        holder.tvTitle.text = listItem.title
        holder.tvDescription.text = listItem.description
        Glide.with(context).load(listItem.thumb).into(holder.ivThumbnail)
        val stLog = "title: ${listItem.title}, des: ${listItem.description}, img: ${listItem.thumb}"
        Log.e("ViRelaAd", stLog)
    }
    private fun isLikes(id: String?, likeButton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(id!!)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()){
                    likeButton.setImageResource(R.drawable.ic_likee)
                    likeButton.tag= "Liked"
                }
                else {
                    likeButton.setImageResource(R.drawable.ic_like)
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}