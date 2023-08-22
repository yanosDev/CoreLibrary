package de.yanos.libraries.util.prefs

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.yanos.core.utils.PreferenceItem
import javax.inject.Inject

interface AppSettings {
    var userId: String
}

class AppSettingsImpl @Inject constructor(@ApplicationContext context: Context) : AppSettings {
    override var userId: String by PreferenceItem(context) { "" }
}