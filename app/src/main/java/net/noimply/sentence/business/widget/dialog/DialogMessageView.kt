package net.noimply.sentence.business.widget.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import net.noimply.sentence.R
import net.noimply.sentence.databinding.WidgetDialogMessageBinding

class DialogMessageView private constructor(private val ownerActivity: Activity) {

    companion object {
        fun create(ownerActivity: Activity) = DialogMessageView(ownerActivity)
    }

    private val vb: WidgetDialogMessageBinding by lazy {
        WidgetDialogMessageBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }

    private val dialog: AlertDialog by lazy {
        AlertDialog
            .Builder(ownerActivity, R.style.Sentence_Widget_Dialog)
            .setView(vb.root)
            .create()
    }

    private var allowPositive = false
    private var allowNegative = false

    fun setTitle(title: CharSequence) = apply {
        vb.title.text = title
        vb.title.isVisible = true
    }

    fun setTitle(@StringRes resourceID: Int) = apply {
        vb.title.setText(resourceID)
        vb.title.isVisible = true
    }

    fun setMessage(message: CharSequence) = apply {
        vb.message.text = message
        vb.message.isVisible = true
    }

    fun setMessage(@StringRes resourceID: Int) = apply {
        vb.message.setText(resourceID)
        vb.message.isVisible = true
    }

    fun setPositiveButton(callback: () -> Unit = {}) = apply {
        allowPositive = true
        vb.buttonPositive.isVisible = true
        vb.buttonPositive.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setPositiveButton(text: CharSequence, callback: () -> Unit = {}) = apply {
        allowPositive = true
        vb.buttonPositive.text = text
        vb.buttonPositive.isVisible = true
        vb.buttonPositive.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setPositiveButton(@StringRes resourceID: Int, callback: () -> Unit = {}) = apply {
        allowPositive = true
        vb.buttonPositive.setText(resourceID)
        vb.buttonPositive.isVisible = true
        vb.buttonPositive.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setNegativeButton(callback: () -> Unit = {}) = apply {
        allowNegative = true
        vb.buttonNegative.isVisible = true
        vb.buttonNegative.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setNegativeButton(text: CharSequence, callback: () -> Unit = {}) = apply {
        allowNegative = true
        vb.buttonNegative.text = text
        vb.buttonNegative.isVisible = true
        vb.buttonNegative.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setNegativeButton(@StringRes resourceID: Int, callback: () -> Unit = {}) = apply {
        allowNegative = true
        vb.buttonNegative.setText(resourceID)
        vb.buttonNegative.isVisible = true
        vb.buttonNegative.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun show(cancelable: Boolean) {
        if (ownerActivity.isFinishing) {
            return
        }
        // prevent human error
        if (!allowPositive && !allowNegative) {
            vb.buttonPositive.isVisible = true
            vb.buttonPositive.setOnClickListener {
                dialog.dismiss()
            }
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