package net.noimply.sentence.business.widget.dialog

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import net.noimply.sentence.R

class DialogLoadingView private constructor(private val activity: Activity) {

    companion object {
        fun create(ownerActivity: Activity) = DialogLoadingView(ownerActivity)
    }

    private val dialog: AppCompatDialog by lazy {
        AppCompatDialog(activity).apply {
            this.setContentView(R.layout.widget_dialog_loading)
            this.setOwnerActivity(activity)
            this.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun show(cancelable: Boolean) {
        this.dialog.setCanceledOnTouchOutside(cancelable)
        this.dialog.show()
    }

    fun dismiss() {
        this.dialog.dismiss()
    }

    fun isShowing(): Boolean {
        return this.dialog.isShowing
    }

}