package net.noimply.sentence.business.support

import android.app.Activity
import net.noimply.sentence.R
import net.noimply.sentence.business.widget.dialog.DialogMessageView

internal object DialogManager {
    fun showNotice(activity: Activity, callback: () -> Unit = {}) {
        val notice = RemoteConfig.appNoticeModel
        val noticeCheck = SentenceSettings.noticeCheck
        if (notice.noticeID.isNotEmpty() && noticeCheck != notice.noticeID) {
            DialogMessageView
                .create(ownerActivity = activity)
                .setTitle(resourceID = R.string.sentence_app_name)
                .setMessage(message = notice.noticeMessage)
                .setPositiveButton {
                    SentenceSettings.noticeCheck = notice.noticeID
                    callback()
                }
                .show(cancelable = false)
        } else {
            callback()
        }
    }
}