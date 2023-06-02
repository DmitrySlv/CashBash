package com.dscreate_app.cashbash

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.dscreate_app.cashbash.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    private fun init() = with(binding) {
        val toggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout, mainContent.toolbar, R.string.open, R.string.close
        )
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this@MainActivity, R.color.white)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this@MainActivity)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_ads -> {
                Toast.makeText(this, "Pressed my_ads", Toast.LENGTH_SHORT).show()
            }
            R.id.cars -> {
                Toast.makeText(this, "Pressed cars", Toast.LENGTH_SHORT).show()
            }
            R.id.pc -> {
                Toast.makeText(this, "Pressed pc", Toast.LENGTH_SHORT).show()
            }
            R.id.smart -> {
                Toast.makeText(this, "Pressed smart", Toast.LENGTH_SHORT).show()
            }
            R.id.dm -> {
                Toast.makeText(this, "Pressed dm", Toast.LENGTH_SHORT).show()
            }
            R.id.sign_up -> {
                Toast.makeText(this, "Pressed sign_up", Toast.LENGTH_SHORT).show()
            }
            R.id.sign_in -> {
                Toast.makeText(this, "Pressed sign_in", Toast.LENGTH_SHORT).show()
            }
            R.id.sign_out -> {
                Toast.makeText(this, "Pressed sign_out", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}