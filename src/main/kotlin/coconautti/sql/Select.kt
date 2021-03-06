package coconautti.sql

class Select(database: Database, private val table: String) : Query(database) {
    private val columns = ArrayList<Column>()
    private var where: Operation? = null
    private var orderBy: OrderBy? = null
    private var limit: Int? = null
    private var offset: Int? = null
    private var fetchNext: Int? = null

    fun columns(vararg columns: String): Select {
        this.columns.addAll(columns.map { Column(it) })
        return this
    }

    fun where(clause: Operation): Select {
        where = clause
        return this
    }

    fun orderBy(column: String): OrderBy {
        val orderBy = OrderBy(column)
        this.orderBy = orderBy
        return orderBy
    }

    fun limit(by: Int): Select {
        limit = by
        return this
    }

    fun offset(by: Int): Select {
        offset = by
        return this
    }

    fun fetchNext(rows: Int): Select {
        fetchNext = rows
        return this
    }

    private val selection: String
        get() = (if (columns.isEmpty()) "*" else columns.joinToString())

    override fun columns(): List<Column> = columns

    override fun values(): List<Value> {
        val clause = where
        clause ?: return emptyList()
        return clause.values()
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("SELECT $selection FROM $table")

        val clause = where
        clause?.let {
            sb.append(" WHERE $clause")
        }

        orderBy?.let { orderBy ->
            sb.append(" $orderBy")
        }

        limit?.let { by ->
            sb.append(" LIMIT $by")
        }

        offset?.let { by ->
            sb.append(" OFFSET $by ROWS")
        }

        fetchNext?.let { rows ->
            sb.append(" FETCH NEXT $rows ROWS ONLY")
        }

        return sb.toString()
    }
}
