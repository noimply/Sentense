package net.noimply.sentence.ui.intro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import net.noimply.sentence.BuildConfig
import net.noimply.sentence.business.support.permissionDrawOverlays
import net.noimply.sentence.databinding.ActivityIntroBinding
import net.noimply.sentence.ui.base.BaseActivity
import net.noimply.sentence.ui.permission.PermissionActivity
import net.noimply.sentence.ui.screen.ScreenActivity


class IntroActivity : BaseActivity() {

    val vb: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    private open class LeakHandler : android.os.Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {}
    }

    private val leakHandler = LeakHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.appVersion.text = BuildConfig.X_BUILD_VERSION_NAME
        leakHandler.postDelayed({ moveToScreen() }, 750)
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
}