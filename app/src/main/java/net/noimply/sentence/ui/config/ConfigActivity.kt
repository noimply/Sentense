package net.noimply.sentence.ui.config

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import net.noimply.sentence.BuildConfig
import net.noimply.sentence.R
import net.noimply.sentence.business.model.DatetimeType
import net.noimply.sentence.business.service.SentenceService
import net.noimply.sentence.business.support.RemoteConfig
import net.noimply.sentence.business.support.SentenceSettings
import net.noimply.sentence.business.support.open
import net.noimply.sentence.business.support.permissionDrawOverlays
import net.noimply.sentence.business.widget.dialog.DialogOverlayView
import net.noimply.sentence.databinding.ActivityConfigBinding
import net.noimply.sentence.ui.base.BaseScreenOverActivity

class ConfigActivity : BaseScreenOverActivity() {

    companion object {
        fun open(activity: Activity) {
            activity.open(intent = Intent(activity, ConfigActivity::class.java))
        }
    }

    private val vb: ActivityConfigBinding by lazy {
        ActivityConfigBinding.inflate(LayoutInflater.from(this))
    }

    private val intentForMarket: Intent by lazy {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(RemoteConfig.appVersionModel.marketUrl)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // app-version
        viewConfigAppVersion()
        // close
        vb.headerClose.setOnClickListener {
            finish()
        }
        // market
        vb.settingAppRating.setOnClickListener {
            startActivity(intentForMarket)
        }
        // suggest
        vb.settingSuggest.setOnClickListener {
            requestSuggest()
        }
        // region { lock-screen-datetime-type }
        when (SentenceSettings.datetimeType) {
            DatetimeType.TIME_24 -> vb.settingDatetime24.isChecked = true
            DatetimeType.TIME_AM_PM -> vb.settingDatetime12.isChecked = true
        }
        vb.settingDatetime.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == vb.settingDatetime24.id) {
                SentenceSettings.datetimeType = DatetimeType.TIME_24
            } else if (checkedId == vb.settingDatetime12.id) {
                SentenceSettings.datetimeType = DatetimeType.TIME_AM_PM
            }
        }
        // endregion
        // region { lock-screen-active }
        vb.settingLockscreenActive.isChecked = SentenceSettings.allowActive
        vb.settingLockscreenActive.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // service start
                screenServiceStart()
            } else {
                // service stop
                stopService(Intent(this@ConfigActivity, SentenceService::class.java))
                vb.settingLockscreenActive.isChecked = false
            }
        }
        vb.settingLockscreenVibrate.isChecked = SentenceSettings.allowVibrate
        vb.settingLockscreenVibrate.setOnCheckedChangeListener { _, isChecked ->
            SentenceSettings.allowVibrate = isChecked
        }
        // endregion
    }

    private fun screenServiceStart() {
        if (permissionDrawOverlays) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this@ConfigActivity, SentenceService::class.java))
                vb.settingLockscreenActive.isChecked = true
            } else {
                startService(Intent(this@ConfigActivity, SentenceService::class.java))
                vb.settingLockscreenActive.isChecked = true
            }
        } else {
            messageView.show(cancelable = false)
        }
    }

    private val messageView: DialogOverlayView by lazy {
        DialogOverlayView.create(this@ConfigActivity)
            .setPositiveButton(launcher = permissionCanDrawOverlayLauncher)
            .setNegativeButton(callback = {
                vb.settingLockscreenActive.isChecked = false
            })
    }

    private val permissionCanDrawOverlayLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (permissionDrawOverlays) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this@ConfigActivity, SentenceService::class.java))
                vb.settingLockscreenActive.isChecked = true
            } else {
                startService(Intent(this@ConfigActivity, SentenceService::class.java))
                vb.settingLockscreenActive.isChecked = true
            }
        } else {
            vb.settingLockscreenActive.isChecked = false
        }
    }

    private fun viewConfigAppVersion() {
        vb.settingAppVersionName.text = BuildConfig.X_BUILD_VERSION_NAME
    }

    private fun requestSuggest() {
        try {
            val emailAddress = "mailto:noimply@gmail.com"
            val subject = "?subject=" + Uri.encode("문의 및 개선사항")
            val body = ("&body="
                    + Uri.encode("\n\n\n\n\n\n\n\n\n")
                    + Uri.encode("\n--------------------")
                    + Uri.encode("\nSystem: Android")
                    + Uri.encode("\nDevice: " + Build.MODEL)
                    + Uri.encode("\nOSVersion: " + Build.VERSION.SDK_INT)
                    + Uri.encode("\nAppVersionCode: " + "" + BuildConfig.X_BUILD_VERSION_CODE)
                    + Uri.encode("\nAppVersionName: " + BuildConfig.X_BUILD_VERSION_NAME)
                    )
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(emailAddress + subject + body)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, R.string.sentence_common_error, Toast.LENGTH_SHORT).show()
        }
    }
}