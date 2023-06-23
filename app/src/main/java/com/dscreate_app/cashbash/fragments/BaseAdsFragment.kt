package com.dscreate_app.cashbash.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

open class BaseAdsFragment: Fragment(), InterAdsClose {
    lateinit var adView: AdView
    var interAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadInterAd()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAds()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun initAds() {
        MobileAds.initialize(requireActivity())
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun loadInterAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireContext(),
            INTER_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(iAd: InterstitialAd) {
                    interAd = iAd
                }
            }
        )
    }

    fun showInterAd() {
        if (interAd != null) {
            interAd?.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    onCloseInterAd()
                }
                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onCloseInterAd()
                }
            }
            interAd?.show(requireActivity())
        } else {
            onCloseInterAd()
        }
    }

    override fun onCloseInterAd() {
    }

    companion object {
        private const val INTER_AD_ID = "ca-app-pub-3940256099942544/1033173712"
    }
}