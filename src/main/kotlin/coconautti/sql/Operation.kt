package coconautti.sql

interface Operation {
    fun values(): List<Value>
}

abstract class Op(internal val lh: Column, internal val rh: Value) : Operation {
    infix fun and(other: Op) = And(this, other)
    infix fun or(other: Op) = Or(this, other)
    override fun values(): List<Value> = listOf(rh)
}

class Equal(lh: Column, rh: Value) : Op(lh, rh) {
    override fun toString(): String = "$lh = ?"
}

class NotEqual(lh: Column, rh: Value) : Op(lh, rh) {
    override fun toString(): String = "$lh != ?"
}

class GreaterThan(lh: Column, rh: Value) : Op(lh, rh) {
    override fun toString(): String = "$lh > ?"
}
class GreaterThanOrEqual(lh: Column, rh: Value) : Op(lh, rh) {
    override fun toString(): String = "$lh >= ?"
}

class LessThan(lh: Column, rh: Value) : Op(lh, rh) {
    override fun toString(): String = "$lh < ?"
}

class LessThanOrEqual(lh: Column, rh: Value) : Op(lh, rh) {
    override fun toString(): String = "$lh <= ?"
}

abstract class CompositeOp(internal val lh: Op, internal val rh: Op) : Operation {
    override fun values(): List<Value> = listOf(lh.values().first(), rh.values().first())
}

class And(lh: Op, rh: Op) : CompositeOp(lh, rh) {
    override fun toString(): String = "$lh AND $rh"
}

class Or(lh: Op, rh: Op) : CompositeOp(lh, rh) {
    override fun toString(): String = "$lh OR $rh"
}

infix fun String.eq(value: String): Op = Equal(Column(this), Value(value))
infix fun String.eq(value: Long): Op = Equal(Column(this), Value(value))
infix fun String.eq(value: Value): Op = Equal(Column(this), value)
infix fun String.ne(value: String): Op = NotEqual(Column(this), Value(value))
infix fun String.ne(value: Long): Op = NotEqual(Column(this), Value(value))
infix fun String.ne(value: Value): Op = NotEqual(Column(this), value)
infix fun String.lt(value: Long): Op = LessThan(Column(this), Value(value))
infix fun String.lt(value: Value): Op = LessThan(Column(this), value)
infix fun String.lte(value: Long): Op = LessThanOrEqual(Column(this), Value(value))
infix fun String.lte(value: Value): Op = LessThanOrEqual(Column(this), value)
infix fun String.gt(value: Long): Op = GreaterThan(Column(this), Value(value))
infix fun String.gt(value: Value): Op = GreaterThan(Column(this), value)
infix fun String.gte(value: Long): Op = GreaterThanOrEqual(Column(this), Value(value))
infix fun String.gte(value: Value): Op = GreaterThanOrEqual(Column(this), value)
