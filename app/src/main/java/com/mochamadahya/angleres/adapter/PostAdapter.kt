package com.mochamadahya.angleres.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mochamadahya.angleres.MainActivity
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.activity.CommentActivity
import com.mochamadahya.angleres.activity.DetailActivity
import com.mochamadahya.angleres.model.Post
import com.mochamadahya.angleres.model.User
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream


class PostAdapter(private val mContext: Context, private val mPost: List<Post>)
    : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val post = mPost[position]

//
        Picasso.get().load(post.getPostImage()).into(holder.postImage)

//
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("judul", post.getJudul())
            bundle.putString("description", post.getDescription())
            bundle.putString("like", post.getNumberLikes())
            bundle.putString("postimage", post.getPostImage())


            mContext.startActivity(Intent(mContext, DetailActivity::class.java).putExtras(bundle))
        }

        if (post.getJudul().equals("")){
            holder.judul.visibility = View.GONE
        } else {
            holder.judul.visibility = View.VISIBLE
            holder.judul.setText(post.getJudul())
        }

        if (post.getPublisher().equals("")){
            holder.publisher.visibility = View.GONE
        } else {
            holder.publisher.visibility = View.GONE
            holder.publisher.setText("ID : " + post.getPublisher())
        }

        if (post.getDescription().equals("")){
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.setText(post.getDescription())
        }

        publisherInfo(holder.judul, holder.publisher, post.getPublisher())
        isLikes(post.getPostId(), holder.likeButton)
        numberOfLikes(holder.likes, post.getPostId())
        getTotalComment(holder.comments, post.getPostId())

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like"){
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostId()).child(firebaseUser!!.uid)
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(post.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }

        holder.commentButton.setOnClickListener {
            val intentComment = Intent(mContext, CommentActivity::class.java)
            intentComment.putExtra("postId", post.getPostId())
            intentComment.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment)
        }
        holder.comments.setOnClickListener {
            val intentComment2 = Intent(mContext, CommentActivity::class.java)
            intentComment2.putExtra("postId", post.getPostId())
            intentComment2.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intentComment2)
        }
    }

    private fun getTotalComment(comments: TextView, postId: String) {
        val commentRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    comments.text = "Lihat semua " + snapshot.childrenCount.toString() + " Komentar"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun numberOfLikes(likes: TextView, postId: String) {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    likes.text = p0.childrenCount.toString() + " Likes"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun isLikes(postId: String, likeButton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.child(firebaseUser!!.uid).exists()) {
                    likeButton.setImageResource(R.drawable.ic_likee)
                    likeButton.tag = "Liked"
                } else {
                    likeButton.setImageResource(R.drawable.ic_like)
                    likeButton.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    class PostViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

            var postImage: ImageView = itemView.findViewById(R.id.post_image)
            var likeButton: ImageView = itemView.findViewById(R.id.post_image_like)
            var commentButton: ImageView = itemView.findViewById(R.id.post_image_comment)
            var shareButton: ImageView = itemView.findViewById(R.id.post_image_share)
            var judul: TextView = itemView.findViewById(R.id.txt_judul_post)
            var likes: TextView = itemView.findViewById(R.id.post_like)
            var publisher: TextView = itemView.findViewById(R.id.post_publisher)
            var description: TextView = itemView.findViewById(R.id.post_description)
            var comments: TextView = itemView.findViewById(R.id.post_comments)
    }

    private fun publisherInfo(
        judul: TextView,
        publisher: TextView,
        publisherID: String
    ) {


        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(publisherID)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    judul.text = user?.getNickname()
                    publisher.text = user?.getNickname()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}