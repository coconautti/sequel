package coconautti.sql

class Insert(database: Database, private val table: String) : Statement(database) {
    private val columns = ArrayList<Column>()
    private val values = ArrayList<Value>()

    fun columns(vararg columns: String): Insert {
        this.columns.addAll(columns.map { Column(it) })
        return this
    }

    fun values(vararg values: Any?): Insert {
        if (values is Array<out Any?>) {
            this.values.addAll(values.toList().map { Value(it) })
        } else {
            this.values.add(Value(values))
        }
        return this
    }

    override fun values(): List<Value> = values

    override fun toString(): String {
        val params = values.map { "?" }
        return "INSERT INTO $table (${columns.joinToString()}) VALUES (${params.joinToString()})"
    }

    override fun toString(dialect: SQLDialect): String {
        return when (dialect) {
            SQLDialect.POSTGRESQL -> "${toString()} RETURNING *"
            else -> toString()
        }
    }
}
