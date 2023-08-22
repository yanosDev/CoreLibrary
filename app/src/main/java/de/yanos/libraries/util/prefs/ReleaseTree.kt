package de.yanos.libraries.util.prefs

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree(private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()) : Timber.Tree() {
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