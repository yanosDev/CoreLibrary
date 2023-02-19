package de.yanos.firestorewrapper.domain

sealed class Condition(open val priority: Int) {
    class Limit(val count: Long) : Condition(0)
    class OrderByAscending(val field: String) : Condition(1)
    class OrderByDescending(val field: String) : Condition(2)
    class WhereArrayContains(val field: String, val value: Any) : Condition(3)
    class WhereLessThan(val field: String, val value: Any) : Condition(4)
    class WhereGreaterThan(val field: String, val value: Any) : Condition(5)
    class WhereIn(val field: String, val values: List<Any>) : Condition(6)
    class WhereEquals(val field: String,  val value: Any) : Condition(7)
}
