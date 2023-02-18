package de.yanos.corelibrary.utils

import android.graphics.Rect
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

enum class ContentType {
    SINGLE, DUAL
}

enum class ContentPosition {
    TOP, CENTER
}

enum class NavigationType {
    BOTTOM, RAIL, DRAWER
}

data class ScreenConfig(
    private val windowSize: WindowSizeClass,
    private val displayFeatures: List<DisplayFeature>,
) {
    val navigationType: NavigationType
    val contentType: ContentType
    val contentPosition: ContentPosition

    init {
        val foldingFeature = displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()

        val foldingDevicePosture = when {
            isBookPosture(foldingFeature) ->
                DevicePosture.BookPosture(foldingFeature.bounds)

            isSeparating(foldingFeature) ->
                DevicePosture.Separating(foldingFeature.bounds, foldingFeature.orientation)

            else -> DevicePosture.NormalPosture
        }
        contentPosition = when (windowSize.heightSizeClass) {
            WindowHeightSizeClass.Compact -> ContentPosition.TOP
            WindowHeightSizeClass.Medium, WindowHeightSizeClass.Expanded -> ContentPosition.CENTER
            else -> ContentPosition.TOP
        }
        when {
            windowSize.widthSizeClass == WindowWidthSizeClass.Compact -> Pair(NavigationType.BOTTOM, ContentType.SINGLE)
            windowSize.widthSizeClass == WindowWidthSizeClass.Medium && foldingDevicePosture != DevicePosture.NormalPosture -> Pair(
                NavigationType.RAIL,
                ContentType.DUAL
            )
            windowSize.widthSizeClass == WindowWidthSizeClass.Medium -> Pair(NavigationType.RAIL, ContentType.SINGLE)
            windowSize.widthSizeClass == WindowWidthSizeClass.Expanded && foldingDevicePosture is DevicePosture.BookPosture -> Pair(
                NavigationType.RAIL,
                ContentType.DUAL
            )
            windowSize.widthSizeClass == WindowWidthSizeClass.Expanded -> Pair(NavigationType.DRAWER, ContentType.DUAL)
            else -> Pair(NavigationType.BOTTOM, ContentType.SINGLE)
        }.let { (navigation, content) ->
            navigationType = navigation
            contentType = content
        }
    }
}

@OptIn(ExperimentalContracts::class)
fun isBookPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}

@OptIn(ExperimentalContracts::class)
fun isSeparating(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.FLAT && foldFeature.isSeparating
}

sealed interface DevicePosture {
    object NormalPosture : DevicePosture

    data class BookPosture(
        val hingePosition: Rect
    ) : DevicePosture

    data class Separating(
        val hingePosition: Rect,
        var orientation: FoldingFeature.Orientation
    ) : DevicePosture
}