package de.yanos.corelibrary.utils

import android.content.Context
import kotlin.reflect.KProperty

internal class PreferenceItem<T : Any> constructor(
    private val context: Context,
    private val preferenceFileName: String = "default",
    private val initializer: (() -> T)
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        synchronized(this) {
            val preferences = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
            @Suppress("UNCHECKED_CAST")
            return preferences.all[property.name] as? T ?: let {
                val value = initializer()
                setValue(thisRef, property, value)

                value
            }
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        synchronized(this) {
            val preference = context.getSharedPreferences(preferenceFileName, Context.MODE_PRIVATE)
            val editor = preference.edit()
            when (value) {
                is String -> editor.putString(property.name, value as? String)
                is Int -> editor.putInt(property.name, value as Int)
                is Float -> editor.putFloat(property.name, value as Float)
                is Boolean -> editor.putBoolean(property.name, value as Boolean)
                is Long -> editor.putLong(property.name, value as Long)
            }
            editor.apply()
        }
    }
}
