package de.yanos.corelibrary.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Small Font",
    group = "Font",
    fontScale = 0.5f
)
@Preview(
    name = "Large Font",
    group = "Font",
    fontScale = 1.5f
)
annotation class FontScalePreviews


@Preview(
    name = "Night Mode",
    group = "Theme",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Light Mode",
    group = "Theme",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
annotation class AppThemePreviews

@Preview(
    name = "Phone",
    group = "Device",
    device = Devices.PIXEL_4
)
@Preview(
    name = "Tablet",
    group = "Device",
    device = Devices.TABLET
)
annotation class DevicePreviews

@DevicePreviews
@FontScalePreviews
@AppThemePreviews
@Preview(name = "All", group = "All", showSystemUi = true)
annotation class AllPreviews


@Preview(
    name = "Phone",
    group = "Device",
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_YES

)
@Preview(
    name = "Tablet",
    group = "Device",
    device = Devices.TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    name = "Phone",
    group = "Device",
    device = Devices.PIXEL_4,
    uiMode = Configuration.UI_MODE_NIGHT_NO

)
@Preview(
    name = "Tablet",
    group = "Device",
    device = Devices.TABLET,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
annotation class SonayPreviews