package net.noimply.sentence.business

import android.app.Activity
import android.app.Application

class AppContext : Application() {

    init {
        instance = this@AppContext
    }

    companion object {
        private var instance: AppContext? = null

        fun contextor(): AppContext {
            if (instance == null) {
                throw IllegalStateException("Application dose not exists")
            } else {
                return instance as AppContext
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        instance = null
    }

    fun exit(activity: Activity) {
        activity.finishAffinity()
    }
}