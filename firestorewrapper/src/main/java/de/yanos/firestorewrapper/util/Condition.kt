package de.yanos.firestorewrapper.util

sealed class Condition(open val priority: Int, val uniqueId: String) {
    class OrderByAscending(val field: String) : Condition(0, field)
    class OrderByDescending(val field: String) : Condition(1, field)
    class WhereArrayContains(val field: String, val value: Any) : Condition(2, value.toString())
    class WhereLessThan(val field: String, val value: Any) : Condition(3, value.toString())
    class WhereGreaterThan(val field: String, val value: Any) : Condition(4, value.toString())
    class WhereIn(val field: String, val values: List<Any>) : Condition(5, values.joinToString { "::" })
    class WhereEquals(val field: String, val value: Any) : Condition(6, value.toString())
    class StartAfter(val value: String) : Condition(7, value)
    class EndBefore(val value: String) : Condition(8, value)
    class StartAt(val value: String) : Condition(9, value)
    class EndAt(val value: String) : Condition(10, value)
    class Limit(val count: Long) : Condition(11, count.toString())
}