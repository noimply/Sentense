package net.noimply.sentence.business.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import net.noimply.sentence.R
import net.noimply.sentence.business.AppConstants
import net.noimply.sentence.business.support.Logger
import net.noimply.sentence.ui.intro.IntroActivity
import net.noimply.sentence.ui.screen.ScreenActivity
import java.util.*

class SentenceService : Service() {

    companion object {
        val NAME: String = SentenceService::class.java.simpleName
        const val notificationId: Int = 999002
        const val channelName: String = "첫화면 영어 알림창 상태바"
        const val channelId: String = "net.noimply.sentence::notification"

        fun start(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, SentenceService::class.java))
            } else {
                context.startService(Intent(context, SentenceService::class.java))
            }
        }

        fun showNotificationWithFullScreen(context: Context) {
            val contentIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(context, IntroActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }, 0
            )
            val fullscreenIntent = PendingIntent.getActivity(
                context,
                0,
                Intent(
                    context,
                    ScreenActivity::class.java
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                }, PendingIntent.FLAG_UPDATE_CURRENT
            )
            val notification = makeNotification(context = context, contentIntent = contentIntent, fullscreenIntent = fullscreenIntent)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId, notification)
        }

        private fun makeNotification(context: Context, contentIntent: PendingIntent? = null, fullscreenIntent: PendingIntent? = null): Notification {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
                NotificationCompat.Builder(context, channelId)
            } else {
                NotificationCompat.Builder(context)
            }.apply {
                setOngoing(true)
                setOnlyAlertOnce(true)
                setAutoCancel(true)
                setSmallIcon(R.mipmap.ic_launcher_round)
                setContentTitle(context.getString(R.string.sentence_notification_content_title))
                setContentText(context.getString(R.string.sentence_notification_content_text))
                contentIntent?.let { setContentIntent(it) }
                fullscreenIntent?.let {
                    setLocalOnly(true)
                    setCategory(NotificationCompat.CATEGORY_ALARM)
                    setFullScreenIntent(it, true)
                }
            }.build()
        }
    }

    private var isRegisterReceiver: Boolean = false

    override fun onCreate() {
        Logger.info { "$NAME -> onCreate()" }
        super.onCreate()
    }

    override fun onDestroy() {
        Logger.info { "$NAME -> onDestroy()" }
        super.onDestroy()
        unregisterReceivers()
        startAlarmTimer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Logger.info { "$NAME -> onBind()" }
        return null
    }

    override fun onRebind(intent: Intent?) {
        Logger.info { "$NAME -> onRebind()" }
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Logger.info { "$NAME -> onUnbind()" }
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.info { "$NAME -> onStartCommand { { intent: ${intent?.toString() ?: "null"}, flag: $flags, startId : $startId }" }
        intent?.let {
            registerBroadcastReceiver()
            registerNotification()
        }
        return Service.START_STICKY
    }

    private fun registerNotification() {
        Logger.info { "$NAME -> registerNotification" }
        val notificationCompatBuilder = makeNotification(
            context = this@SentenceService,
            contentIntent = makeContentIntent(),
            fullscreenIntent = null
        )
        startForeground(notificationId, notificationCompatBuilder)
    }

    private fun makeContentIntent(): PendingIntent {
        return PendingIntent.getActivity(
            this@SentenceService,
            0,
            Intent(this@SentenceService, IntroActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            },
            0
        )
    }

    private fun registerBroadcastReceiver() {
        registerReceiver(lockScreenActionReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF).apply {
                priority = IntentFilter.SYSTEM_LOW_PRIORITY
            }
            addAction(Intent.ACTION_USER_PRESENT)
        })
        isRegisterReceiver = true
    }

    private fun unregisterReceivers() {
        if (isRegisterReceiver) {
            try {
                unregisterReceiver(lockScreenActionReceiver)
            } catch (e: Exception) {
                Logger.error(e) {
                    "$NAME -> unregisterReceivers { screenOffActionReceiver, screenOnActionReceiver,  userPresentActionReceiver }"
                }
            }
        }
    }

    private val lockScreenActionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val allowCanDrawOverlays = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.canDrawOverlays(context)
            } else {
                true
            }
            Logger.info { "$NAME -> lockScreenActionReceiver { action: ACTION_SCREEN_ON -> canDrawOverlays: $allowCanDrawOverlays }" }
            when (intent?.action) {
                Intent.ACTION_SCREEN_ON -> {
                    context?.let {
                        Logger.info { "$NAME -> lockScreenActionReceiver { action: ACTION_SCREEN_ON }" }
                        val callState = (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).callState
                        if (callState == 0) {
                            if (!allowCanDrawOverlays) {
                                ScreenActivity.open(it)
                                showNotificationWithFullScreen(context = it)
                                Logger.info { "$NAME -> lockScreenActionReceiver -> showNotificationWithFullScreen" }
                            }
                        }
                    }
                }
                Intent.ACTION_SCREEN_OFF -> {
                    context?.let {
                        val callState = (getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).callState
                        if (callState == 0) {
                            if (allowCanDrawOverlays) {
                                ScreenActivity.open(it)
                                Logger.info { "$NAME -> lockScreenActionReceiver -> ScreenActivity -> Open" }
                            }
                            Logger.info { "$NAME -> lockScreenActionReceiver -> XceneLockerActivity -> start { action: ACTION_SCREEN_OFF, callState: idle }" }
                        } else {
                            Logger.info { "$NAME -> lockScreenActionReceiver -> XceneLockerActivity -> start { action: ACTION_SCREEN_OFF, callState: $callState }" }
                        }
                    }
                }
                Intent.ACTION_USER_PRESENT -> {
                    Logger.info { "$NAME -> lockScreenActionReceiver { action: ACTION_USER_PRESENT }" }
                }
            }
        }
    }

    // region { service to live }
    private fun startAlarmTimer() {
        val c: Calendar = Calendar.getInstance()
        c.timeInMillis = System.currentTimeMillis()
        c.add(Calendar.SECOND, 1)
        val intent = Intent(this, SentenceService::class.java).apply {
            action = AppConstants.Action.ACTION_NEED_SERVICE_TO_LIVE
        }
        val sender: PendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val mAlarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, c.timeInMillis, sender)
        Logger.info { "$NAME -> startAlarmTimer" }
    }
    // endregion

}