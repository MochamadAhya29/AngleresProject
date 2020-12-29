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
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.activity.*
import com.mochamadahya.angleres.videostreamer.ApiHelperInterface
import com.mochamadahya.angleres.videostreamer.CommonUtil
import com.mochamadahya.angleres.videostreamer.CommonUtil.internetDialog
import com.mochamadahya.angleres.videostreamer.VideoAllAdapters
import com.mochamadahya.angleres.videostreamer.VideoModel
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
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
    private var rv_video_home: RecyclerView? = null
    private var btn_video_Add: ImageView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_home, container, false)

        rv_video_home = root.findViewById(R.id.rv_video_home)


        if (!CommonUtil.isOnline(root.context)){
            internetDialog()
        }



        val carouselView = root.findViewById(R.id.carouselViewHome) as CarouselView;
        carouselView.setPageCount(sampleImages.size)
        carouselView.setImageListener(imageListener)

        btn_video_Add?.setOnClickListener {
            startActivity(Intent(context, AddVideoBeritaActivity::class.java))
        }
        rv_video_home?.visibility = View.GONE
        shimmer_layout?.visibility = View.VISIBLE
        shimmer_layout?.startShimmer()
        getVideoData()

        return root
    } var imageListener: ImageListener = object : ImageListener {
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
            startActivity(Intent(context, AddVideoBeritaActivity::class.java))
        }


    }

    private fun getVideoData() {
        ApiHelperInterface.create().videoData().enqueue(object : Callback<ArrayList<VideoModel>> {
            override fun onFailure(call: Call<ArrayList<VideoModel>>, t: Throwable) {
                CommonUtil.showMessage(activity!!, "Please Try after some time")
            }

            override fun onResponse(
                call: Call<ArrayList<VideoModel>>,
                response: Response<ArrayList<VideoModel>>
            ) {
                val videoList: ArrayList<VideoModel> = response.body()!!
                rv_video_home?.layoutManager = LinearLayoutManager(activity!!, LinearLayoutManager.VERTICAL, false)
                rv_video_home?.adapter = VideoAllAdapters(activity!!, videoList)
                shimmer_layout?.stopShimmer()
                shimmer_layout?.visibility = View.GONE
                rv_video_home?.visibility = View.VISIBLE
            }

        })
    }

    override fun onResume() {
        super.onResume()
        shimmer_layout?.startShimmer()
    }

    override fun onPause() {
        shimmer_layout?.stopShimmer()
        super.onPause()
    }


    fun internetDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setMessage("Please Connect to Internet")
        builder.setCancelable(false).setIcon(R.drawable.ic_internet)
            .setTitle("No Internet Connection")
        builder.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            dialog.cancel()
            activity?.finish()
        }.setNegativeButton("Retry") { dialog, _ ->
            if (!CommonUtil.isOnline(requireActivity().applicationContext)) {
                internetDialog()
            } else {
                dialog.dismiss()
                getVideoData()
            }
        }
        val alert = builder.create()
        alert.show()
    }

}