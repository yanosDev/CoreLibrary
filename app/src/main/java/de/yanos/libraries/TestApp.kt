package de.yanos.libraries

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import de.yanos.libraries.util.prefs.ReleaseTree
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class TestApp : Application() {
    @Inject lateinit var crashlytics: FirebaseCrashlytics
    override fun onCreate() {
        super.onCreate()
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree(crashlytics))
    }
}