package net.noimply.sentence.ui.intro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import net.noimply.sentence.business.AppConstants
import net.noimply.sentence.BuildConfig
import net.noimply.sentence.business.support.Logger
import net.noimply.sentence.business.support.SentenceSettings
import net.noimply.sentence.business.support.permissionDrawOverlays
import net.noimply.sentence.databinding.ActivityIntroBinding
import net.noimply.sentence.ui.base.BaseActivity
import net.noimply.sentence.ui.permission.PermissionActivity
import net.noimply.sentence.ui.screen.ScreenActivity


class IntroActivity : BaseActivity() {

    val vb: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    protected open class LeakHandler : android.os.Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {}
    }

    protected val leakHandler = LeakHandler()
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.appVersion.text = BuildConfig.X_BUILD_VERSION_NAME
        // MobileAds
        MobileAds.initialize(this) {
            Logger.info { "MobileAds -> initialize -> checkExecuteCount" }
            checkExecuteCount()
        }
    }

    private fun moveToScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!permissionDrawOverlays) {
                PermissionActivity.open(activity = this)
                finish()
            } else {
                ScreenActivity.open(this)
                finish()
            }
        }, 1500L)
    }

    private fun checkExecuteCount() {
        Logger.info { "checkExecuteCount -> { execute-count: ${SentenceSettings.executeCount} }" }
        if (SentenceSettings.executeCount > 100) {
            requestInterstitial()
        } else {
            SentenceSettings.executeCount++
            leakHandler.postDelayed({ moveToScreen() }, 1500)
        }
    }

    private fun requestInterstitial() {
        InterstitialAd.load(
            this,
            AppConstants.PlacementID.appOpen,
            AdRequest.Builder().build(),
            interstitialAdLoadCallback
        )
    }

    private val interstitialAdLoadCallback = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: InterstitialAd) {
            this@IntroActivity.interstitialAd = interstitialAd
            this@IntroActivity.interstitialAd?.fullScreenContentCallback = fullScreenContentCallback
            this@IntroActivity.interstitialAd?.let {
                leakHandler.postDelayed({ it.show(this@IntroActivity) }, 1500)
            } ?: moveToScreen()
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
            leakHandler.postDelayed({ moveToScreen() }, 1500)
        }
    }

    private val fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
            Logger.info { "InterstitialAd -> onAdShowedFullScreenContent" }
            leakHandler.postDelayed({ moveToScreen() }, 750L)
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
            Logger.error { "InterstitialAd -> onAdFailedToShowFullScreenContent { error: ${adError?.message} }" }
            moveToScreen()
        }

        override fun onAdShowedFullScreenContent() {
            Logger.info { "InterstitialAd -> onAdShowedFullScreenContent" }
            interstitialAd = null
        }
    }
}