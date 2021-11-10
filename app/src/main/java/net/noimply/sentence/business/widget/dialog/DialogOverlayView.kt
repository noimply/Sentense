package net.noimply.sentence.business.widget.dialog

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import net.noimply.sentence.R
import net.noimply.sentence.business.support.toHtml
import net.noimply.sentence.databinding.WidgetDialogOverlayBinding

class DialogOverlayView private constructor(private val ownerActivity: Activity) {

    companion object {
        fun create(ownerActivity: Activity) = DialogOverlayView(ownerActivity)
    }

    private val vb: WidgetDialogOverlayBinding by lazy {
        WidgetDialogOverlayBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }

    private val dialog: AlertDialog by lazy {
        AlertDialog
            .Builder(ownerActivity, R.style.Sentence_Widget_Dialog)
            .setView(vb.root)
            .create()
    }

    init {
        vb.message.text = ownerActivity.getString(R.string.sentence_dialog_permission_overlay_message).toHtml
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun setPositiveButton(launcher: ActivityResultLauncher<Intent>) = apply {
        vb.buttonPositive.setOnClickListener {
            dialog.dismiss()
            launcher.launch(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:net.noimply.sentence"))
            )
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