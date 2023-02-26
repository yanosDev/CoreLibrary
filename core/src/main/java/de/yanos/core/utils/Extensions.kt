package de.yanos.core.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue


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