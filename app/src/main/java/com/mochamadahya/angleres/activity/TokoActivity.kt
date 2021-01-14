package com.mochamadahya.angleres.activity

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.mochamadahya.angleres.R

class TokoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toko)

        val sp = getSharedPreferences("Name", Context.MODE_PRIVATE)
        val spedit = sp.edit()
        spedit.putString("key", "value")
        spedit.apply()

        setTitle("Toko")
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toko_item, menu)
        return true
    }
}