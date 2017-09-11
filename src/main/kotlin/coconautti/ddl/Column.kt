package coconautti.ddl

import coconautti.sql.Value

abstract class Column(val name: String) {
    private var primaryKey = false
    private var autoIncrement = false
    private var unique = false
    private var nullable = false
    private var default: Value? = null

    fun primaryKey(): Column {
        primaryKey = true
        return this
    }

    fun autoIncrement(): Column {
        autoIncrement = true
        return this
    }

    fun unique(): Column {
        unique = true
        return this
    }

    fun nullable(): Column {
        nullable = true
        return this
    }

    fun default(value: Any?): Column {
        default = Value(value)
        return this
    }

    abstract fun type(): String

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(name)
        sb.append(type())

        if (unique) {
            sb.append(" UNIQUE")
        }

        if (autoIncrement) {
            sb.append(" AUTO_INCREMENT")
        }

        if (primaryKey) {
            sb.append(" PRIMARY KEY")
        } else if (!nullable) {
            sb.append(" NOT NULL")
        }

        default?.let {
            sb.append(" DEFAULT ${default.toString()}")
        }

        return sb.toString()
    }
}

class Varchar(name: String, private val length: Int) : Column(name) {
    override fun type(): String = " VARCHAR($length)"
}

class Bigint(name: String) : Column(name) {
    override fun type(): String = " BIGINT"
}

class Clob(name: String) : Column(name) {
    override fun type(): String = " CLOB"
}

class Timestamp(name: String) : Column(name) {
    override fun type(): String = " TIMESTAMP"
}

class Bool(name: String) : Column(name) {
    override fun type(): String = " BOOLEAN"
}

class Bigserial(name: String) : Column(name) {
    override fun type(): String = " BIGSERIAL"
}

class Jsonb(name: String) : Column(name) {
    override fun type(): String = " JSONB"
}

class Text(name: String) : Column(name) {
    override fun type(): String = " TEXT"
}
