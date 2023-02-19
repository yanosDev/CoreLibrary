package de.yanos.firestorewrapper.domain

sealed class Condition(open val priority: Int, val uniqueId: String) {
    class Limit(val count: Long) : Condition(0, count.toString())
    class OrderByAscending(val field: String) : Condition(1, field)
    class OrderByDescending(val field: String) : Condition(2, field)
    class WhereArrayContains(val field: String, val value: Any) : Condition(3, value.toString())
    class WhereLessThan(val field: String, val value: Any) : Condition(4, value.toString())
    class WhereGreaterThan(val field: String, val value: Any) : Condition(5, value.toString())
    class WhereIn(val field: String, val values: List<Any>) : Condition(6, values.joinToString { "::" })
    class WhereEquals(val field: String, val value: Any) : Condition(7, value.toString())
}