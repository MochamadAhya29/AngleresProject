package com.mochamadahya.angleres

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mochamadahya.angleres.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_nav.setOnNavigationItemSelectedListener (onBottomNavListener)

        val frag = supportFragmentManager.beginTransaction()
        frag.add(R.id.frame_container,
            HomeFragment()
        )
        frag.commit()
    }

    private val onBottomNavListener = BottomNavigationView.OnNavigationItemSelectedListener { i->

        var selectedFragment: Fragment = HomeFragment()
        when(i.itemId){
            R.id.itemHome -> {
                selectedFragment = HomeFragment()
            }

            R.id.itemFeed -> {
                selectedFragment = FeedFragment()
            }

            R.id.itemPesan -> {
                selectedFragment = PesanFragment()
            }

            R.id.itemTroli -> {
                selectedFragment = TroliFragment()
            }

            R.id.itemAkun -> {
                selectedFragment = ProfileFragment()
            }
        }
        val frag = supportFragmentManager.beginTransaction()
        frag.replace(R.id.frame_container, selectedFragment)
        frag.commit()

        true

    }

}