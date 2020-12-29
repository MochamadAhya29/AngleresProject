package com.mochamadahya.angleres.activity.lokasi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.mochamadahya.angleres.R

class KolamActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kolam)

        setTitle("Kolam")
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.lokasi_item, menu)
        return true
    }
}