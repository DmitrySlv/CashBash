package com.dscreate_app.cashbash.activities

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dscreate_app.cashbash.R
import com.dscreate_app.cashbash.adapters.AdsAdapter
import com.dscreate_app.cashbash.data.models.AdModelDto
import com.dscreate_app.cashbash.databinding.ActivityMainBinding
import com.dscreate_app.cashbash.utils.dialogs.DialogHelper
import com.dscreate_app.cashbash.utils.firebase.AccountHelper
import com.dscreate_app.cashbash.utils.showToast
import com.dscreate_app.cashbash.view_model.FirebaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(),
    OnNavigationItemSelectedListener, AdsAdapter.DeleteItemListener, AdsAdapter.AdViewedListener,
        AdsAdapter.FavouriteClicked
{

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val adsAdapter = AdsAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true
    private var currentCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initNavViewAndToolbar()
        initRcView()
        initViewModel()
        activityResult()
        bottomMenuClick()
        scrollListener()
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.main
    }

    private fun activityResult() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    Log.d("MyLog", "Api 0")
                    account.idToken?.let { idToken ->
                        dialogHelper.accHelper.signInFirebaseWithGoogle(idToken)
                    }
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error: ${e.message}")
            }
        }
    }

    private fun initNavViewAndToolbar() = with(binding) {
        currentCategory = getString(R.string.b_nav_main)
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
        tvAccount = navView.getHeaderView(HEADER_INDEX_POS).findViewById(R.id.tvAccEmail)
        imAccount = navView.getHeaderView(HEADER_INDEX_POS).findViewById(R.id.imAccImage)
        navViewSettings()
    }

    private fun initRcView() = with(binding) {
        mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
        mainContent.rcView.adapter = adsAdapter

    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this) {
            val list = getAdsByCategory(it)
            if (!clearUpdate) {
                adsAdapter.updateAdapter(list)
            } else {
                adsAdapter.updateAdapterWithClear(list)
            }
            binding.mainContent.tvEmpty.visibility = if (adsAdapter.itemCount == ITEM_COUNT_EMPTY) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun getAdsByCategory(list: MutableList<AdModelDto>): MutableList<AdModelDto> {
        val tempList = mutableListOf<AdModelDto>()
        tempList.addAll(list)
        if (currentCategory != getString(R.string.b_nav_main)) {
            tempList.clear()
            list.forEach {
                if (currentCategory == it.category) {
                    tempList.add(it)
                }
            }
        }
        tempList.reverse()
        return tempList
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clearUpdate = true
        when (item.itemId) {
            R.id.main -> {
                showToast("Главная")
            }
            R.id.my_ads -> {
                showToast("Мои Объявления")
            }
            R.id.favourite -> {
                showToast("Избранное")
            }
            R.id.cars -> {
              getAdsCat(getString(R.string.ad_cars))
            }
            R.id.pc -> {
                getAdsCat(getString(R.string.ad_pc))
            }
            R.id.smart -> {
                getAdsCat(getString(R.string.ad_smartphones))
            }
            R.id.dm -> {
                getAdsCat(getString(R.string.ad_domestics))
            }
            R.id.sign_up -> {
                dialogHelper.createSignDialog(DialogHelper.SIGN_UP_STATE)
            }
            R.id.sign_in -> {
                dialogHelper.createSignDialog(DialogHelper.SIGN_IN_STATE)
            }
            R.id.sign_out -> {
                if (mAuth.currentUser?.isAnonymous == true) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutGoogle()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getAdsCat(cat: String) {
        currentCategory = cat
        firebaseViewModel.loadAllAdsFromCat(cat)
    }

    private fun bottomMenuClick() = with(binding) {
        mainContent.bNavView.setOnItemSelectedListener { item ->
            clearUpdate = true
            when(item.itemId) {
                R.id.new_ad -> {
                    val intent = Intent(this@MainActivity, EditAdsActivity::class.java)
                    startActivity(intent)
                }
                R.id.my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.favs -> {
                    firebaseViewModel.loadMyFavourites()
                    mainContent.toolbar.title = getString(R.string.ad_favourite)
                }
                R.id.main -> {
                    currentCategory = getString(R.string.b_nav_main)
                    firebaseViewModel.loadAllAdsFirstPage()
                    mainContent.toolbar.title = getString(R.string.b_nav_main)
                }
            }
            true
        }
    }

    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            dialogHelper.accHelper.signInAnonymously(object: AccountHelper.CompleteListener {
                override fun onComplete() {
                    tvAccount.text = getString(R.string.anonymous)
                    imAccount.setImageResource(R.drawable.baseline_account_def)
                }
            })
        } else if (user.isAnonymous){
            tvAccount.text = getString(R.string.anonymous)
            imAccount.setImageResource(R.drawable.baseline_account_def)
        } else {
            tvAccount.text = user.email
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
    }

    override fun deleteItem(adModel: AdModelDto) {
        firebaseViewModel.deleteItem(adModel, this)
    }

    override fun adViewed(adModel: AdModelDto) {
        firebaseViewModel.adViewed(adModel)

        val i = Intent(this, DescriptionActivity::class.java)
        i.putExtra(AD_MODEL_DATA, adModel)
        startActivity(i)
    }

    override fun favouriteClicked(adModel: AdModelDto) {
        firebaseViewModel.onFavouriteClick(adModel)
    }

    private fun navViewSettings() = with(binding) {
        val menu = navView.menu
        val adsCat = menu.findItem(R.id.ads_cat)
        val spanAdsCat = SpannableString(adsCat.title)
        adsCat.title?.length?.let {
            spanAdsCat.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.red)),
                SPAN_START, it, SPAN_FLAGS
            )
            adsCat.title = spanAdsCat
        }

        val accCat = menu.findItem(R.id.acc_cat)
        val spanAccCat = SpannableString(accCat.title)
        accCat.title?.length?.let {
            spanAccCat.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.red)),
                SPAN_START, it, SPAN_FLAGS
            )
            accCat.title = spanAccCat
        }
    }

    private fun scrollListener() = with(binding.mainContent) {
        rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(SCROLL_DOWN) &&
                    newState == RecyclerView.SCROLL_STATE_IDLE) {
                    clearUpdate = false
                    val adsList = firebaseViewModel.liveAdsData.value
                    if (adsList?.isNotEmpty() == true) {
                        getAdsFromCat(adsList)
                    }
                }
            }
        })
    }

    private fun getAdsFromCat(adsList: MutableList<AdModelDto>) {
        adsList[0].let {
            if (currentCategory == getString(R.string.b_nav_main)) {
                it.time?.let { time ->
                    firebaseViewModel.loadAllAdsNextPage(it.time)
                }
            } else {
                val catTime = "${it.category}_${it.time}"
                firebaseViewModel.loadAllAdsFromCatNextPage(catTime)
            }
        }
    }

    companion object {
        private const val HEADER_INDEX_POS = 0
        const val EDIT_STATE = "edit_state"
        const val AD_MODEL_DATA = "ad_model_data"
        private const val SPAN_START = 0
        private const val SPAN_FLAGS = 0
        private const val SCROLL_DOWN = 1
        private const val SCROLL_UP = -1
        private const val ITEM_COUNT_EMPTY = 0
        private const val LAST_TIME = "0"
        private const val TAG = "MyLog"
    }
}