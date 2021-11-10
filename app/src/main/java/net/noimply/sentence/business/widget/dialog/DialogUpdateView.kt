package net.noimply.sentence.business.widget.dialog

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.core.view.isVisible
import net.noimply.sentence.BuildConfig
import net.noimply.sentence.R
import net.noimply.sentence.business.support.RemoteConfig
import net.noimply.sentence.business.support.SentenceSettings
import net.noimply.sentence.databinding.WidgetDialogUdpateBinding
import org.joda.time.DateTime

class DialogUpdateView private constructor(private val ownerActivity: Activity) {

    companion object {
        //fun create(ownerActivity: Activity) = DialogUpdateView(ownerActivity)
        fun checkAndCreate(ownerActivity: Activity): DialogUpdateView? {
            var dialog: DialogUpdateView? = null
            // check version
            if (BuildConfig.X_BUILD_VERSION_CODE < RemoteConfig.appVersionModel.versionCode) {
                // check date
                val currentTime = DateTime().millis
                if (SentenceSettings.updateCheckDate < currentTime) {
                    dialog = DialogUpdateView(ownerActivity).apply {
                        setMessage(RemoteConfig.appVersionModel.description)
                    }
                }
            }
            return dialog
        }
    }

    private val vb: WidgetDialogUdpateBinding by lazy {
        WidgetDialogUdpateBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }

    private val dialog: AlertDialog by lazy {
        AlertDialog
            .Builder(ownerActivity, R.style.Sentence_Widget_Dialog)
            .setView(vb.root)
            .create()
    }

    var dialogCallback: IDialogCallback? = null

    init {
        vb.buttonPositive.setOnClickListener {
            dialog.dismiss()
            // go market
            ownerActivity.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(RemoteConfig.appVersionModel.marketUrl)
                }
            )
            dialogCallback?.onPositive()
        }
        vb.buttonNegative.setOnClickListener {
            SentenceSettings.updateCheckDate = DateTime().plusDays(7).millis
            dialog.dismiss()
            dialogCallback?.onNegative()
        }
    }

    fun setMessage(message: CharSequence) = apply {
        vb.message.text = message
        vb.message.isVisible = true
    }

    fun setPositiveButton(callback: () -> Unit = {}) = apply {
        vb.buttonPositive.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setNegativeButton(callback: () -> Unit = {}) = apply {
        vb.buttonNegative.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun show(cancelable: Boolean) {
        if (ownerActivity.isFinishing) {
            return
        }
        dialog.setCancelable(cancelable)
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    fun isShowing(): Boolean {
        return this.dialog.isShowing
    }
}