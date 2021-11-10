package net.noimply.sentence.ui.permission

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import net.noimply.sentence.R
import net.noimply.sentence.business.support.SentenceSettings
import net.noimply.sentence.business.support.permissionDrawOverlays
import net.noimply.sentence.business.support.toHtml
import net.noimply.sentence.business.widget.dialog.DialogOverlayView
import net.noimply.sentence.databinding.ActivityPermissionBinding
import net.noimply.sentence.ui.base.BaseActivity
import net.noimply.sentence.ui.screen.ScreenActivity

class PermissionActivity : BaseActivity() {

    companion object {
        fun open(activity: Activity) {
            activity.startActivity(Intent(activity, PermissionActivity::class.java))
        }
    }

    private val vb: ActivityPermissionBinding by lazy {
        ActivityPermissionBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        vb.permissionCanDrawOverlayDescription.text = getString(R.string.sentence_permission_can_draw_overlay_description).toHtml
        // start check permission
        vb.buttonConfirm.setOnClickListener {
            if (SentenceSettings.allowActive) {
                if (permissionDrawOverlays) {
                    ScreenActivity.open(context = this@PermissionActivity)
                    finish()
                } else {
                    permissionOverlayDialog.show(cancelable = false)
                }
            } else {
                ScreenActivity.open(context = this@PermissionActivity)
                finish()
            }
        }
    }

    private val permissionOverlayDialog: DialogOverlayView by lazy {
        DialogOverlayView.create(this@PermissionActivity)
            .setPositiveButton(launcher = permissionCanDrawOverlayLauncher)
            .setNegativeButton(callback = {
                ScreenActivity.open(context = this@PermissionActivity)
                finish()
            })
    }

    private val permissionCanDrawOverlayLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (permissionDrawOverlays) {
            ScreenActivity.open(context = this@PermissionActivity)
            finish()
        }
    }

    override fun onBackPressed() {
        return
    }

}