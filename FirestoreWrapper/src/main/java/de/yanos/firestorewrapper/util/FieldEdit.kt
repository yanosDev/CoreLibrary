package de.yanos.firestorewrapper.util

sealed class FieldEdit {
    object Delete : FieldEdit()
    class ArrayRemove(val ids: List<String>) : FieldEdit()
    class ArrayAdd(val ids: List<String>) : FieldEdit()
}