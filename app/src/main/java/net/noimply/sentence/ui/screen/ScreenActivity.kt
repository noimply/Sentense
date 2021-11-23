package net.noimply.sentence.ui.screen

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.*
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.google.android.gms.ads.*
import net.noimply.sentence.business.AppConstants
import net.noimply.sentence.R
import net.noimply.sentence.business.model.DatetimeType
import net.noimply.sentence.business.service.SentenceService
import net.noimply.sentence.business.support.*
import net.noimply.sentence.business.viewmodel.ResultViewModel
import net.noimply.sentence.business.viewmodel.ScreenViewModel
import net.noimply.sentence.business.widget.SwipeBackLayout
import net.noimply.sentence.business.widget.dialog.DialogLoadingView
import net.noimply.sentence.business.widget.dialog.DialogUpdateView
import net.noimply.sentence.databinding.ActivityScreenBinding
import net.noimply.sentence.ui.config.ConfigActivity
import org.joda.time.DateTime
import java.util.*


class ScreenActivity : AppCompatActivity() {

    companion object {
        val NAME: String = ScreenActivity::class.java.simpleName
        fun open(context: Context) {
            val intent = Intent(context, ScreenActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
            context.startActivity(intent)
        }
    }

    private val bannerView: AdView by lazy {
        AdView(this).apply {
            adSize = AdSize.BANNER
            adUnitId = AppConstants.PlacementID.appCommonLinear
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    vb.bannerFrame.isVisible = false
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    vb.bannerFrame.isVisible = true
                }
            }
        }
    }

    private val drawOverlayIntent: Intent by lazy {
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
    }

    private val activityForResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (permissionDrawOverlays) {
            vb.permissionOverlayContainer.isVisible = false
        }
    }

    private val vb: ActivityScreenBinding by lazy {
        ActivityScreenBinding.inflate(LayoutInflater.from(this))
    }

    private val dialogLoadingView: DialogLoadingView by lazy {
        DialogLoadingView.create(this@ScreenActivity)
    }

    private val receiverHomeKey: ReceiverHomeKey by lazy {
        ReceiverHomeKey()
    }

    private val viewModel by viewModels<ScreenViewModel>()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.info { "$NAME -> onCreate" }
        window.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        registerReceiver(receiverLockScreen, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_TIME_TICK)
        })
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // background
        setContentBackground()
        // start lockscreen service
        if (SentenceSettings.allowActive) {
            Logger.info { "$NAME -> SentenceService.start" }
            SentenceService.start(this@ScreenActivity)
        }
        // swipe
        initSwipeBackLayout()
        // click-event
        vb.menuConfig.setOnClickListener {
            ConfigActivity.open(this)
        }
        // view-model
        viewModel.content.observe(this, {
            if (it is ResultViewModel.Complete) {
                vb.sentenceEnglish.text = it.success.sentenceEnglish
                vb.sentenceKorean.text = it.success.sentenceKorea
            }
        })

        DialogManager.showNotice(activity = this@ScreenActivity) {
            DialogUpdateView.checkAndCreate(ownerActivity = this@ScreenActivity)?.show(cancelable = false)
        }

        // banner
        if (SentenceSettings.executeCount > 20) {
            MobileAds.initialize(this) {
                vb.bannerFrame.isVisible = true
                vb.bannerFrame.addView(bannerView)
                bannerView.loadAd(AdRequest.Builder().build())
            }
        } else {
            SentenceSettings.executeCount++
            vb.bannerFrame.isVisible = false
        }
    }

    private fun setContentBackground() {
        val index = Random().nextInt(3)
        if (index == 0) {
            val viewColor = Color.parseColor("#333333")
            vb.screenWrapper.setBackgroundResource(R.drawable.scene_null_001)
            vb.screenDay.setTextColor(viewColor)
            vb.screenDate.setTextColor(viewColor)
            vb.screenTime.setTextColor(viewColor)
            vb.menuConfig.setColorFilter(viewColor)
            vb.sentenceIcon.setColorFilter(viewColor)
            vb.sentenceKorean.setTextColor(viewColor)
            vb.sentenceEnglish.setTextColor(viewColor)
            return
        }
        if (index == 1) {
            val viewColor = Color.parseColor("#FFFFFF")
            vb.screenWrapper.setBackgroundResource(R.drawable.scene_null_002)
            vb.screenDay.setTextColor(viewColor)
            vb.screenDate.setTextColor(viewColor)
            vb.screenTime.setTextColor(viewColor)
            vb.menuConfig.setColorFilter(viewColor)
            vb.sentenceIcon.setColorFilter(viewColor)
            vb.sentenceKorean.setTextColor(viewColor)
            vb.sentenceEnglish.setTextColor(viewColor)
            return
        }
        if (index == 2) {
            val viewColor = Color.parseColor("#FFFFFF")
            vb.screenWrapper.setBackgroundResource(R.drawable.scene_null_003)
            vb.screenDay.setTextColor(viewColor)
            vb.screenDate.setTextColor(viewColor)
            vb.screenTime.setTextColor(viewColor)
            vb.menuConfig.setColorFilter(viewColor)
            vb.sentenceIcon.setColorFilter(viewColor)
            vb.sentenceKorean.setTextColor(viewColor)
            vb.sentenceKorean.setBackgroundColor(Color.parseColor("#4C000000"))
            vb.sentenceEnglish.setTextColor(viewColor)
            vb.sentenceEnglish.setBackgroundColor(Color.parseColor("#4C000000"))
            return
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        Logger.info { "$NAME -> onWindowFocusChanged" }
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        Logger.info { "$NAME -> onAttachedToWindow" }
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        super.onAttachedToWindow()
    }

    override fun onNewIntent(intent: Intent?) {
        Logger.info { "$NAME -> onNewIntent" }
        super.onNewIntent(intent)
    }

    override fun onStart() {
        Logger.info { "$NAME -> onStart" }
        super.onStart()
    }

    override fun onStop() {
        Logger.info { "$NAME -> onStop" }
        super.onStop()
    }

    override fun onResume() {
        Logger.info { "$NAME -> onResume { innerActionTimeTick, checkPermissionDrawOverlays, receiverHomeKey.register }" }
        super.onResume()
        // new sentence
        viewModel.requestSentence()
        // time tick
        innerActionTimeTick()
        // overlay
        checkPermissionDrawOverlays()
        // receiver home-key
        receiverHomeKey.register()
    }

    override fun onPause() {
        Logger.info { "$NAME -> onPause { receiverHomeKey.unregister }" }
        super.onPause()
        // receiver home-key
        receiverHomeKey.unregister()
    }

    override fun onDestroy() {
        Logger.info { "$NAME -> onDestroy" }
        super.onDestroy()
        try {
            unregisterReceiver(receiverLockScreen)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event)
//    }

    override fun onBackPressed() {
        /*
        cashButtonLayout?.onBackPressed(callback = object:ICashButtonBackPressedListener {
            override fun onBackPressed(isSuccess: Boolean) {

            }
        })
        //super.onBackPressed()
         */
    }

    private fun initSwipeBackLayout() {
        vb.swipeBackLayout.isSwipeFromEdge = false
        vb.swipeBackLayout.setSwipeBackListener(object : SwipeBackLayout.OnSwipeBackListener {
            override fun onViewPositionChanged(mView: View?, swipeBackFraction: Float, swipeBackFactor: Float) {
                //SceneUtil.vibrate(50)
            }

            override fun onViewSwipeFinished(mView: View?, isEnd: Boolean) {
                if (isEnd) {
                    finish()
                }
            }
        })
    }

    private fun checkPermissionDrawOverlays() {
        if (permissionDrawOverlays || !SentenceSettings.allowActive) {
            vb.permissionOverlayContainer.isVisible = false
            return
        }
        vb.permissionOverlayContainer.isVisible = true
        // message
        vb.permissionOverlayDescription.text = getString(R.string.sentence_permission_can_draw_overlay_screen_message).toHtml
        // button
        vb.permissionOverlaySetting.setOnClickListener {
            activityForResultLauncher.launch(drawOverlayIntent)
        }
        vb.permissionOverlayLater.setOnClickListener {
            vb.permissionOverlayContainer.isVisible = false
        }
    }

    private fun innerActionTimeTick() {
        Handler(mainLooper).post {
            val current = DateTime()
            vb.screenDate.text = "${current.toString("yyyy. M. d.")}"
            vb.screenDay.text = "${AppConstants.weekNamesEng[current.dayOfWeek - 1]}"
            when (SentenceSettings.datetimeType) {
                DatetimeType.TIME_24 -> vb.screenTime.text = current.toString("HH:mm")
                DatetimeType.TIME_AM_PM -> vb.screenTime.text = current.toString("a") + current.toString("hh:mm")
            }
        }
    }

    private fun innerActionScreenOn() {
        Logger.info { "$NAME ->  innerActionScreenOn" }
    }

    private fun innerActionScreenOff() {
        Logger.info { "$NAME ->  innerActionScreenOff" }
    }

    // region { screen receiver }
    private val receiverLockScreen: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionName = intent?.action ?: ""
            if (actionName == Intent.ACTION_SCREEN_ON) {
                Logger.info { "$NAME -> lockScreenReceiver { action: ACTION_SCREEN_ON }" }
                innerActionScreenOn()
                return
            }

            if (actionName == Intent.ACTION_SCREEN_OFF) {
                Logger.info { "$NAME -> lockScreenReceiver { action: ACTION_SCREEN_OFF }" }
                innerActionScreenOff()
                return
            }

            if (actionName == Intent.ACTION_TIME_TICK) {
                Logger.info { "$NAME -> lockScreenReceiver { action: ACTION_TIME_TICK }" }
                innerActionTimeTick()
                return
            }
        }
    }
    // endregion

    // region { home key receiver }
    inner class ReceiverHomeKey : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val actionName = it.action
                if (actionName.equals(other = Intent.ACTION_CLOSE_SYSTEM_DIALOGS, ignoreCase = true)) {
                    it.getStringExtra("reason")?.let { r ->
                        if (r.equals(other = "homekey", ignoreCase = true)) {
                            // add window
                            this@ScreenActivity.addLockerView()
                        }
                    }
                }
                Logger.info {
                    "$NAME -> HomeKeyReceiver -> onReceive { actionName: $actionName, reason: ${it.getStringExtra("reason")} }"
                }
            }
        }

        fun register() {
            this.unregister()
            this@ScreenActivity.registerReceiver(this, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        }

        fun unregister() {
            try {
                this@ScreenActivity.unregisterReceiver(this)
            } catch (e: Exception) {
                Logger.error {
                    "$NAME -> HomeKeyReceiver -> unregister { exception: ${e.message} }"
                }
            }
        }
    }

    private var homeKeyLockerView: RelativeLayout? = null

    @SuppressLint("ClickableViewAccessibility")
    private fun addLockerView() {
        try {
            this@ScreenActivity.finish()
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (homeKeyLockerView != null) {
                removeLockerView(windowManager)
            }
            homeKeyLockerView = LayoutInflater.from(this@ScreenActivity)
                .inflate(R.layout.layout_screen_locker, null) as RelativeLayout

            homeKeyLockerView?.let { locker ->
                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    getType(),
                    524832,
                    PixelFormat.TRANSLUCENT
                )
                locker.setOnClickListener {
                    this.removeLockerView(windowManager)
                }
                locker.findViewById<AppCompatTextView>(R.id.screen_locker_dismiss).setOnClickListener {
                    this@ScreenActivity.finish()
                    removeLockerView(windowManager)
                }
                // gravity
                params.gravity = Gravity.TOP or Gravity.START
                params.x = 0
                params.y = 0
                params.height = vb.root.height + resources.getDimensionPixelOffset(R.dimen.sentence_navigation_height)
                var alpha = 1f
                locker.setOnTouchListener { _, motion ->
                    when (motion?.action) {
                        MotionEvent.ACTION_UP -> {
                            if (alpha < 0.6f) {
                                alpha = 0f
                                locker.alpha = alpha
                                this@ScreenActivity.finish()
                                removeLockerView(windowManager)
                            } else {
                                alpha = 1f
                                locker.alpha = alpha
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            alpha -= 0.07f
                            locker.alpha = alpha
                        }
                        MotionEvent.ACTION_DOWN -> {
                            // nope
                        }
                    }
                    true
                }
                windowManager.addView(locker, params)
            }
        } catch (e: Exception) {
            Logger.error {
                "$NAME -> addLockerView { exception: ${e.message} }"
            }
        }
    }

    private fun removeLockerView(manager: WindowManager) {
        try {
            homeKeyLockerView?.let { locker ->
                manager.removeView(locker)
                homeKeyLockerView = null
            }
        } catch (e: Exception) {
            Logger.error {
                "$NAME -> removeLockerView { exception: ${e.message} }"
            }
        }
    }

    // TYPE_STATUS_BAR = 2000;
    // TYPE_PHONE = 2002
    // TYPE_TOAST = 2005
    // TYPE_PRESENTATION = 2037
    // TYPE_APPLICATION_OVERLAY = 2038
    private fun getType(var1: Int): Int {
        return if (var1 < 2000) {
            var1
        } else if (Build.VERSION.SDK_INT >= 26) {
            if (Settings.canDrawOverlays(this)) 2038 else 2037
        } else {
            if (var1 <= 0) 2005 else var1
        }
    }

    private fun getType(): Int {
        return if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) this.getType(2005) else this.getType(
            2002
        )
    }
    // endregion
}