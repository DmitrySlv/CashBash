package com.dscreate_app.cashbash.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.databinding.ActivityMainBinding
import com.dscreate_app.cashbash.dialogs.DialogHelper
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    private lateinit var tvAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun init() = with(binding) {
        val toggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout, mainContent.toolbar, R.string.open, R.string.close
        )
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this@MainActivity, R.color.white)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this@MainActivity)
        tvAccount = navView.getHeaderView(HEADER_INDEX).findViewById(R.id.tvAccEmail)
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
                dialogHelper.createSignDialog(DialogHelper.SIGN_UP_STATE)
            }
            R.id.sign_in -> {
                dialogHelper.createSignDialog(DialogHelper.SIGN_IN_STATE)
            }
            R.id.sign_out -> {
                uiUpdate(null)
                mAuth.signOut()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text = if (user == null) {
            getString(R.string.not_reg)
        } else {
            user.email
        }
    }

    companion object {
        private const val HEADER_INDEX = 0
    }
}