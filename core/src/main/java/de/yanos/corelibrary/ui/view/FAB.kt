package de.yanos.core.ui.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.yanos.core.R
import de.yanos.core.utils.TAG_FAB

@Composable
fun Fab(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    content: String? = null,
    isLoading: Boolean = false,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(modifier = modifier.testTag(TAG_FAB),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        onClick = {
            if (!isLoading) {
                onClick()
            }
        }) {
        Row(modifier = modifier.padding(16.dp)) {
            if (isLoading)
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            else Icon(imageVector = icon, contentDescription = content)
        }
    }
}

@Composable
fun EditFab(modifier: Modifier = Modifier, isLoading: Boolean = false, onClick: () -> Unit) =
    Fab(modifier = modifier, icon = Icons.Default.Edit, content = stringResource(id = R.string.d_edit), isLoading = isLoading, onClick = onClick)

@Composable
fun AddFab(modifier: Modifier = Modifier, isLoading: Boolean, onClick: () -> Unit) =
    Fab(modifier = modifier, icon = Icons.Default.Add, content = stringResource(id = R.string.d_add), isLoading = isLoading, onClick = onClick)

@Composable
fun ExFab(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Add,
    text: String = "",
    content: String? = null,
    isLoading: Boolean = false,
    onClick: () -> Unit = {}
) {
    ExtendedFloatingActionButton(modifier = modifier.testTag(TAG_FAB),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        onClick = {
            if (!isLoading) {
                onClick()
            }
        }) {
        if (isLoading)
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        else {
            Icon(imageVector = icon, contentDescription = content)
            Text(
                text = stringResource(id = R.string.fab_add_note),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EditExFab(modifier: Modifier = Modifier, isLoading: Boolean = false, onClick: () -> Unit) =
    ExFab(
        modifier = modifier,
        icon = Icons.Default.Edit,
        content = stringResource(id = R.string.d_edit),
        text = stringResource(id = R.string.d_edit),
        isLoading = isLoading,
        onClick = onClick
    )

@Composable
fun AddExFab(modifier: Modifier = Modifier, isLoading: Boolean, onClick: () -> Unit) =
    ExFab(
        modifier = modifier,
        icon = Icons.Default.Add,
        text = stringResource(id = R.string.d_add),
        content = stringResource(id = R.string.d_add),
        isLoading = isLoading,
        onClick = onClick
    )