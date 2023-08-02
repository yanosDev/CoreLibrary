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
    AlertDialog(
        modifier = modifier.testTag(TAG_DIALOG),
        onDismissRequest = onDismiss,
        iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        icon = { if (icon != null) Icon(imageVector = icon, tint = MaterialTheme.colorScheme.onSurfaceVariant, contentDescription = "Dialog Icon") },
        title = { Text(text = stringResource(id = title), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Start) },
        text = { Text(text = stringResource(id = text), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Start) },
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