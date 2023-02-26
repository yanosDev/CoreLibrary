package de.yanos.core.utils

import androidx.annotation.StringDef

@Retention(AnnotationRetention.SOURCE)
@StringDef(
    TAG_FAB,
    TAG_TEXT,
    TAG_BUTTON,
    TAG_SNACK,
    TAG_TOP_BAR,
    TAG_DIALOG,
    TAG_SLIDER,
    TAG_SWITCH,
    TAG_DROPDOWN,
    TAG_DROPDOWN_SELECTION,
    TAG_PLACEHOLDER,
    TAG_SUPPORT,
    TAG_LABEL,
    TAG_MAP,
    TAG_ITEM,
)
annotation class TestTags

const val TAG_FAB = "fab_"
const val TAG_TEXT = "text_"
const val TAG_BUTTON = "btn_"
const val TAG_SNACK = "snack_"
const val TAG_TOP_BAR = "topbar_"
const val TAG_DIALOG = "dialog_"
const val TAG_SLIDER = "slider_"
const val TAG_SWITCH = "switch_"
const val TAG_DROPDOWN = "dropDown_"
const val TAG_DROPDOWN_SELECTION = "dropSelection_"
const val TAG_PLACEHOLDER = "placeholder_"
const val TAG_SUPPORT = "support_"
const val TAG_LABEL = "label_"
const val TAG_MAP = "map_"
const val TAG_ITEM = "item_"