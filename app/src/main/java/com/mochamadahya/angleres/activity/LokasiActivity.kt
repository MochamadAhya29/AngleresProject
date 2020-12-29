package com.mochamadahya.angleres.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import com.mochamadahya.angleres.R
import com.mochamadahya.angleres.activity.lokasi.DanauActivity
import com.mochamadahya.angleres.activity.lokasi.KolamActivity
import com.mochamadahya.angleres.activity.lokasi.LautActivity
import com.mochamadahya.angleres.activity.lokasi.SungaiActivity
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_lokasi.*

class LokasiActivity : AppCompatActivity() {

    var sampleImages = intArrayOf(
        R.drawable.gambar1,
        R.drawable.gambar2
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lokasi)

        setTitle("Lokasi")

        val carouselView = findViewById(R.id.carouselViewLocation) as CarouselView;
        carouselView.setPageCount(sampleImages.size)
        carouselView.setImageListener(imageListener)

        img_sungai.setOnClickListener {
            startActivity(Intent(this, SungaiActivity::class.java))
        }

        img_laut.setOnClickListener {
            startActivity(Intent(this, LautActivity::class.java))
        }

        img_kolam.setOnClickListener {
            startActivity(Intent(this, KolamActivity::class.java))
        }

        img_danau.setOnClickListener {
            startActivity(Intent(this, DanauActivity::class.java))
        }
    }
    var imageListener: ImageListener = object : ImageListener {
        override fun setImageForPosition(position: Int, imageView: ImageView) {
            // You can use Glide or Picasso here
            imageView.setImageResource(sampleImages[position])
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lokasi_item, menu)
        return true

    }
}