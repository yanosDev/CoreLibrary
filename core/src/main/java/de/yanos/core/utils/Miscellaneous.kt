package de.yanos.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Patterns
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.util.regex.Pattern

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

internal fun TextFieldValue.addText(newString: String): TextFieldValue {
    val newText = this.text.replaceRange(
        this.selection.start,
        this.selection.end,
        newString
    )
    val newSelection = TextRange(
        start = newText.length,
        end = newText.length
    )

    return this.copy(text = newText, selection = newSelection)
}

fun String.isName() = isNotBlank() && !Pattern.compile("^[a-zA-ZäÄöÖüÜß ]+([ -][a-zA-Z ]+)*$").matcher(this).matches()
fun String.isNumber() = isNotBlank() && !Pattern.compile(".*[0-9]+.*").matcher(this).matches()
fun String.isEmail() = isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(this).matches()
fun String.isPassword() = isNotBlank() && !Pattern.compile(".{8,}").matcher(this).matches()
fun String.isPhone() = isNotBlank() && !Patterns.PHONE.matcher(this).matches()
fun String.isStreet() = isNotBlank() && !Pattern.compile("[a-zA-ZßäöüÄÖÜ .\\/-]+[ ]?[0-9a-zA-Z ,.\\/-]*$").matcher(this).matches()
fun String.isZip() = isNotBlank() && !Pattern.compile("^[0-9]{4,5}$").matcher(this).matches()
fun String.isCity() = isNotBlank() && !Pattern.compile("[a-zA-ZäöüÄÖÜß .\\/-]+").matcher(this).matches()
fun String.isCountry() = isNotBlank() && !Pattern.compile("^[a-zA-ZäÄöÖüÜß ]+([ -][a-zA-Z ]+)*$").matcher(this).matches()