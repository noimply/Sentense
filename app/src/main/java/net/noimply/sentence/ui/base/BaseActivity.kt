package net.noimply.sentence.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.noimply.sentence.business.widget.dialog.DialogLoadingView

abstract class BaseActivity : AppCompatActivity() {

    var loadingView: DialogLoadingView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingView = DialogLoadingView.create(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingView?.dismiss()
    }

}