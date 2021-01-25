package com.mochamadahya.angleres.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.activity.*
import com.mochamadahya.angleres.adapter.PostAdapter
import com.mochamadahya.angleres.model.Post
import com.mochamadahya.angleres.videostreamer.ApiHelperInterface
import com.mochamadahya.angleres.videostreamer.CommonUtil
import com.mochamadahya.angleres.videostreamer.CommonUtil.internetDialog
import com.mochamadahya.angleres.videostreamer.VideoAllAdapters
import com.mochamadahya.angleres.videostreamer.VideoModel
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_video_stream_player.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList


class HomeFragment : Fragment() {

    var sampleImages = intArrayOf(
        R.drawable.gambar1,
        R.drawable.gambar2
    )
    lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null
    private var rv_video_home: RecyclerView? = null
    private var btn_video_Add: ImageView? = null

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_home, container, false)


        rv_video_home = root.findViewById(R.id.rv_video_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        rv_video_home?.layoutManager = linearLayoutManager


        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        rv_video_home?.adapter = postAdapter

        val carouselView = root.findViewById(R.id.carouselViewHome) as CarouselView;
        carouselView.setPageCount(sampleImages.size)
        carouselView.setImageListener(imageListener)

        getPost()


//        if (!CommonUtil.isOnline(root.context)){
//            internetDialog()
//        }




//        rv_video_home?.visibility = View.GONE
//        shimmer_layout?.visibility = View.VISIBLE
//        shimmer_layout?.startShimmer()
//        getVideoData()

        return root
    }

    private fun getPost() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()

                for (snapshot in p0.children){
                    val post = snapshot.getValue(Post::class.java)
                    post?.let { postList?.add(it) }
                    postAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    var imageListener: ImageListener = object : ImageListener {
        override fun setImageForPosition(position: Int, imageView: ImageView) {
            // You can use Glide or Picasso here
            imageView.setImageResource(sampleImages[position])
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imgLocation: ImageView = view.findViewById(R.id.img_lokasi)
        img_lokasi.setOnClickListener {
            startActivity(Intent(context, LokasiActivity::class.java))
        }
        img_event.setOnClickListener {
            startActivity(Intent(context, EventActivity::class.java))
        }
        img_store.setOnClickListener {
            startActivity(Intent(context, TokoActivity::class.java))
        }
        img_rent.setOnClickListener {
            startActivity(Intent(context, PenyewaanActivity::class.java))
        }
        img_livestreaming.setOnClickListener {
            startActivity(Intent(context, LiveStreamingActivity::class.java))
        }
        img_bisnis.setOnClickListener {
            startActivity(Intent(context, BisnisActivity::class.java))
        }
        img_video.setOnClickListener {
            startActivity(Intent(context, VideoActivity::class.java))
        }


        img_addvideo.setOnClickListener {
            startActivity(Intent(context, UploadingVideoActivity::class.java))
        }


    }

//    private fun getVideoData() {
//        ApiHelperInterface.create().videoData().enqueue(object : Callback<ArrayList<VideoModel>> {
//            override fun onFailure(call: Call<ArrayList<VideoModel>>, t: Throwable) {
//                CommonUtil.showMessage(activity!!, "Please Try after some time")
//            }
//
//            override fun onResponse(
//                call: Call<ArrayList<VideoModel>>,
//                response: Response<ArrayList<VideoModel>>
//            ) {
//                val videoList: ArrayList<VideoModel> = response.body()!!
//                rv_video_home?.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
//                rv_video_home?.adapter = VideoAllAdapters(activity!!, videoList)
//                shimmer_layout?.stopShimmer()
//                shimmer_layout?.visibility = View.GONE
//                rv_video_home?.visibility = View.VISIBLE
//            }
//
//        })
//    }
//
//    override fun onResume() {
//        super.onResume()
//        shimmer_layout?.startShimmer()
//    }
//
//    override fun onPause() {
//        shimmer_layout?.stopShimmer()
//        super.onPause()
//    }
//
//
//    fun internetDialog() {
//        val builder = AlertDialog.Builder(requireActivity())
//        builder.setMessage("Please Connect to Internet")
//        builder.setCancelable(false).setIcon(R.drawable.ic_internet)
//            .setTitle("No Internet Connection")
//        builder.setPositiveButton(
//            "OK"
//        ) { dialog, _ ->
//            dialog.cancel()
//            activity?.finish()
//        }.setNegativeButton("Retry") { dialog, _ ->
//            if (!CommonUtil.isOnline(requireActivity().applicationContext)) {
//                internetDialog()
//            } else {
//                dialog.dismiss()
//                getVideoData()
//            }
//        }
//        val alert = builder.create()
//        alert.show()
//    }

}