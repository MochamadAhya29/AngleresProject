package com.mochamadahya.angleres.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.mochamadahya.angleres.R
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

//        userUid = FirebaseAuth.getInstance().currentUser?.uid
        tv_title_detail.setText(intent.getStringExtra("judul"))
        tv_description_detail.setText(intent.getStringExtra("description"))
        txt_like_detail.setText(intent.getStringExtra("like"))

    }
}