package coconautti.sql

class Update(database: Database, private val table: String) : Statement(database) {
    private val fields = LinkedHashMap<Column, Value>()
    private var where: Operation? = null

    fun set(column: String, value: Any?): Update {
        fields[Column(column)] = Value(value)
        return this
    }

    fun where(clause: Operation): Update {
        where = clause
        return this
    }

    private val sets: String
        get() = fields.map { "${it.key} = ?" }.joinToString()

    override fun values(): List<Value> {
        val values = fields.values.toList()

        val clause = where
        if (clause != null) {
            return values + clause.values()
        } else {
            return values
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("UPDATE $table SET $sets")

        val clause = where
        if (clause != null) {
            sb.append(" WHERE $clause")
        }

        return sb.toString()
    }
}
