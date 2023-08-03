package de.yanos.core.ui.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import de.yanos.core.utils.TAG_BUTTON
import de.yanos.core.utils.TAG_DIALOG
import de.yanos.core.utils.TAG_TEXT

@Composable
fun CustomDialog(
    modifier: Modifier = Modifier,
    confirmTxt: Int = android.R.string.ok,
    cancelTxt: Int = android.R.string.cancel,
    icon: ImageVector? = null,
    title: Int,
    text: Int,
    showCancel: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    CustomDialog(
        modifier = modifier,
        confirmTxt = confirmTxt,
        cancelTxt = cancelTxt,
        icon = icon,
        title = stringResource(id = title),
        text = stringResource(id = text),
        showCancel = showCancel,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
fun CustomDialog(
    modifier: Modifier = Modifier,
    confirmTxt: Int = android.R.string.ok,
    cancelTxt: Int = android.R.string.cancel,
    icon: ImageVector? = null,
    title: String,
    text: String,
    showCancel: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        modifier = modifier.testTag(TAG_DIALOG),
        onDismissRequest = onDismiss,
        icon = { if (icon != null) Icon(imageVector = icon, contentDescription = "Dialog Icon") },
        title = { Text(text = title, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Start) },
        text = { Text(text = text, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Start) },
        confirmButton = {
            TextButton(modifier = Modifier.testTag(TAG_DIALOG + TAG_BUTTON + "1"), onClick = onConfirm) {
                Text(
                    modifier = Modifier.testTag(TAG_DIALOG + TAG_BUTTON + TAG_TEXT + "1"),
                    text = stringResource(id = confirmTxt),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        dismissButton = {
            if (showCancel) {
                TextButton(modifier = Modifier.testTag(TAG_DIALOG + TAG_BUTTON + "2"), onClick = onDismiss, shape = ButtonDefaults.textShape) {
                    Text(
                        modifier = Modifier.testTag(TAG_DIALOG + TAG_BUTTON + TAG_TEXT + "2"),
                        text = stringResource(id = cancelTxt),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    )
}