package coconautti.sql

class BatchInsert(val database: Database, private val table: String) : BatchStatement(database) {
    private val columns = ArrayList<Column>()
    private val values = ArrayList<List<Value>>()

    fun columns(vararg columns: String): BatchInsert {
        this.columns.addAll(columns.map { Column(it) })
        return this
    }

    fun values(vararg values: Any?): BatchInsert {
        if (values is Array<out Any?>) {
            this.values.add(values.toList().map { Value(it) })
        } else {
            this.values.add(listOf(Value(values)))
        }
        return this
    }

    override fun values(): List<List<Value>> = values

    override fun toString(): String {
        val params = values.first().map { "?" }
        return "INSERT INTO $table (${columns.joinToString()}) VALUES (${params.joinToString()})"
    }

    override fun toString(dialect: SQLDialect): String {
        return when (dialect) {
            SQLDialect.POSTGRESQL -> "${toString()} RETURNING id"
            else -> toString()
        }
    }
}
