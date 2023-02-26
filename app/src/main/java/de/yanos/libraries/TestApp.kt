package de.yanos.libraries

import android.app.Application
import de.yanos.crashlog.util.Clog

class TestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Clog.plant()
    }
}