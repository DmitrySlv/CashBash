package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.AdsAdapter
import com.dscreate_app.cashbash.data.DbManager
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.ActivityMainBinding
import com.dscreate_app.cashbash.utils.dialogs.DialogHelper
import com.dscreate_app.cashbash.utils.firebase.GoogleAccountConst.GOOGLE_SIGN_IN_REQUEST_CODE
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(),
    OnNavigationItemSelectedListener,
    DbManager.ReadDataCallback {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
    private lateinit var tvAccount: TextView
    private val dbManager = DbManager(this)
    private val adsAdapter = AdsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initMenus()
        initRcView()
        dbManager.readDataFromDb()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            //Log.d("MyLog", "SignInResult")
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.new_ad) {
            val intent = Intent(this, EditAdsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initMenus() = with(binding) {
        setSupportActionBar(mainContent.toolbar)
        mainContent.toolbar.setTitleTextColor(ContextCompat.getColor(
            this@MainActivity, R.color.white))
        val toggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout, mainContent.toolbar, R.string.open, R.string.close
        )
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(
            this@MainActivity, R.color.white)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this@MainActivity)
        tvAccount = navView.getHeaderView(HEADER_INDEX).findViewById(R.id.tvAccEmail)
    }

    private fun initRcView() = with(binding) {
        mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        mainContent.rcView.adapter = adsAdapter

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.my_ads -> {
                Toast.makeText(this, "Pressed my_ads", Toast.LENGTH_SHORT).show()
            }
            R.id.favourite -> {
                Toast.makeText(this, "Pressed favourite", Toast.LENGTH_SHORT).show()
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
                dialogHelper.accHelper.signOutGoogle()
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

    override fun readData(list: MutableList<AdModelDto>) {
        adsAdapter.updateAdapter(list)
    }

    companion object {
        private const val HEADER_INDEX = 0
    }
}