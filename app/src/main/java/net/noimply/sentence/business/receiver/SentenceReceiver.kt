package net.noimply.sentence.business.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import net.noimply.sentence.business.AppConstants
import net.noimply.sentence.business.service.SentenceService
import net.noimply.sentence.business.support.Logger
import net.noimply.sentence.business.support.SentenceSettings

internal class SentenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            // ACTION_BOOT_COMPLETED
            if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                try {
                    if (SentenceSettings.allowActive) {
                        SentenceService.start(it)
                    }
                } catch (e: Exception) {
                    Logger.error {
                        "SceneReceiver -> ACTION_BOOT_COMPLETED { exception: ${e.message} }"
                    }
                }
            }
            // ACTION_NEED_SERVICE_TO_LIVE
            if (intent?.action.equals(AppConstants.Action.ACTION_NEED_SERVICE_TO_LIVE)) {
                try {
                    if (SentenceSettings.allowActive) {
                        SentenceService.start(it)
                    }
                } catch (e: Exception) {
                    Logger.error {
                        "SceneReceiver -> ACTION_NEED_SERVICE_TO_LIVE { exception: ${e.message} }"
                    }
                }
            }
        }
    }
}