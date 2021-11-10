package net.noimply.sentence.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import net.noimply.sentence.business.support.Logger

abstract class BaseScreenOverActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.info { "${this.javaClass.simpleName} -> onCreate" }
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        Logger.info { "${this.javaClass.simpleName} -> onWindowFocusChanged" }
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        Logger.info { "${this.javaClass.simpleName} -> onAttachedToWindow" }
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        super.onAttachedToWindow()
    }

    override fun onNewIntent(intent: Intent?) {
        Logger.info { "${this.javaClass.simpleName} -> onNewIntent" }
        super.onNewIntent(intent)
    }

    override fun onStart() {
        Logger.info { "${this.javaClass.simpleName} -> onStart" }
        super.onStart()
    }

    override fun onStop() {
        Logger.info { "${this.javaClass.simpleName} -> onStop" }
        super.onStop()
    }

    override fun onResume() {
        Logger.info { "${this.javaClass.simpleName} -> onResume" }
        super.onResume()
    }

    override fun onPause() {
        Logger.info { "${this.javaClass.simpleName} -> onPause" }
        super.onPause()
    }

    override fun onDestroy() {
        Logger.info { "${this.javaClass.simpleName} -> onDestroy" }
        super.onDestroy()
    }
}