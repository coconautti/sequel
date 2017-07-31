package coconautti.sql

class Delete(database: Database, private val table: String) : Statement(database) {
    private var where: Operation? = null

    fun where(clause: Operation): Delete {
        where = clause
        return this
    }

    override fun values(): List<Value> {
        val clause = where
        clause ?: return emptyList()
        return clause.values()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("DELETE FROM $table")

        val clause = where
        clause?.let {
            sb.append(" WHERE $clause")
        }

        return sb.toString()
    }
}
