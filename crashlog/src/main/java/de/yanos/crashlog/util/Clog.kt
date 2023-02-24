package de.yanos.crashlog.util

import android.util.Log
import com.google.firebase.crashlytics.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

object Clog {
    fun plant(crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()) {
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree(crashlytics))
    }

    fun e(msg: String) = Timber.e(msg)
    fun d(msg: String) = Timber.d(msg)
}

internal class ReleaseTree(private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.ERROR) {
            crashlytics.log(message)
            if (t != null) {
                crashlytics.setCustomKey("priority", Log.ERROR)
                crashlytics.setCustomKey("type", t.javaClass.simpleName)
                crashlytics.recordException(t)
                crashlytics.log(t.toString())
            }
        }
    }
}