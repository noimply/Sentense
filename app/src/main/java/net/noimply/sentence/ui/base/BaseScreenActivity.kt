package net.noimply.sentence.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import net.noimply.sentence.business.support.Logger

abstract class BaseScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.info { "${this.javaClass.simpleName} -> onCreate" }
        super.onCreate(savedInstanceState)
        window.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        registerReceiver(lockScreenReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_TIME_TICK)
        })
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
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
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
        try {
            unregisterReceiver(lockScreenReceiver)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event)
    }

    /**
     * ACTION_SCREEN_ON Action Receiver
     */
    private val lockScreenReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionName = intent?.action ?: ""
            if (actionName == Intent.ACTION_SCREEN_ON) {
                Logger.info { "BaseScreenActivity -> lockScreenReceiver { action: ACTION_SCREEN_ON }" }
                innerActionScreenOn()
                return
            }

            if (actionName == Intent.ACTION_SCREEN_OFF) {
                Logger.info { "BaseScreenActivity -> lockScreenReceiver { action: ACTION_SCREEN_OFF }" }
                innerActionScreenOff()
                return
            }

            if (actionName == Intent.ACTION_TIME_TICK) {
                Logger.info { "BaseScreenActivity -> lockScreenReceiver { action: ACTION_TIME_TICK }" }
                innerActionTimeTick()
                return
            }
        }
    }

    protected abstract fun innerActionScreenOn()

    protected abstract fun innerActionScreenOff()

    protected abstract fun innerActionTimeTick()
}