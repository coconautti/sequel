package coconautti.sql

import java.sql.Connection
import kotlin.reflect.KClass

abstract class Statement(private val database: Database) {
    fun execute(): Any? = database.execute(this)
    fun execute(conn: Connection): Any? = database.execute(conn, this)
    abstract fun values(): List<Value>
}

abstract class Query(val database: Database) : Statement(database) {
    fun query(): List<Record> = database.query(this)
    fun <T> fetch(klass: KClass<*>): List<T> = database.fetch(this, klass)
    abstract fun columns(): List<Column>
}

abstract class BatchStatement(private val database: Database) {
    fun execute(): List<Any> = database.execute(this)
    fun execute(conn: Connection): List<Any> = database.execute(conn, this)
    abstract fun values(): List<List<Value>>
}

class Column(private val name: String) {
    override fun toString(): String = name
}

class Value(internal val value: Any?) {
    override fun toString(): String {
        if (value == null) {
            return "NULL"
        } else if (value is String) {
            return "'$value'"
        } else {
            return value.toString()
        }
    }
}
