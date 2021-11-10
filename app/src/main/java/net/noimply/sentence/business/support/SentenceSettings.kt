package net.noimply.sentence.business.support

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import net.noimply.sentence.business.AppContext
import net.noimply.sentence.business.model.DatetimeType

object SentenceSettings {

    private const val preferenceName = "sentence:local-setting"

    private val setting: SharedPreferences by lazy {
        Logger.info { "Settings -> init -> create instance" }
        AppContext.contextor().getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    }

    private const val ALLOW_ACTIVE = "lock-screen:allow-active"
    var allowActive: Boolean
        get() {
            return setting.getBoolean(ALLOW_ACTIVE, true) ?: true
        }
        set(value) {
            setting.edit { putBoolean(ALLOW_ACTIVE, value) }
        }

    private const val ALLOW_VIBRATE = "lock-screen-allow-vibrate"
    var allowVibrate: Boolean
        get() {
            return setting.getBoolean(ALLOW_VIBRATE, true) ?: true
        }
        set(value) {
            setting.edit { putBoolean(ALLOW_VIBRATE, value) }
        }

    private const val DATETIME_TYPE = "lockscreen:datetime-type"
    var datetimeType: DatetimeType
        get() {
            val value = setting.getInt(DATETIME_TYPE, 1) ?: 1
            return if (value == 1) {
                DatetimeType.TIME_24
            } else {
                DatetimeType.TIME_AM_PM
            }
        }
        set(value) {
            setting.edit { putInt(DATETIME_TYPE, value.value) }
        }

    private const val APP_EXECUTE_COUNT = "app:execute-count"
    var executeCount: Int
        get() {
            return setting.getInt(APP_EXECUTE_COUNT, 0)
        }
        set(value) {
            setting.edit { putInt(APP_EXECUTE_COUNT, value) }
        }

    // region { update }
    private const val UPDATE_CHECK_DATE = "version:update-check-date"
    var updateCheckDate: Long
        get() {
            return setting.getLong(UPDATE_CHECK_DATE, 0L)
        }
        set(value) {
            setting.edit { putLong(UPDATE_CHECK_DATE, value) }
        }
    // endregion

    // region { notice }
    private const val APP_NOTICE_CHECK = "app:notice-id"
    var noticeCheck: String
        get() {
            return setting.getString(APP_NOTICE_CHECK, "") ?: ""
        }
        set(value) {
            setting.edit { putString(APP_NOTICE_CHECK, value) }
        }
    // endregion
}