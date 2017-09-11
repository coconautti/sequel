package coconautti.ddl

class CreateTable(private val name: String, private val force: Boolean = true) {
    private val columns = ArrayList<Column>()

    fun varchar(name: String, length: Int): Column {
        val column = Varchar(name, length)
        columns.add(column)
        return column
    }

    fun bigint(name: String): Column {
        val column = Bigint(name)
        columns.add(column)
        return column
    }

    fun clob(name: String): Column {
        val column = Clob(name)
        columns.add(column)
        return column
    }

    fun timestamp(name: String): Column {
        val column = Timestamp(name)
        columns.add(column)
        return column
    }

    fun boolean(name: String): Column {
        val column = Bool(name)
        columns.add(column)
        return column
    }

    fun bigserial(name: String): Column {
        val column = Bigserial(name)
        columns.add(column)
        return column
    }

    fun jsonb(name: String): Column {
        val column = Jsonb(name)
        columns.add(column)
        return column
    }

    fun text(name: String): Column {
        val column = Text(name)
        columns.add(column)
        return column
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("CREATE TABLE")

        if (!force) {
            sb.append(" IF NOT EXISTS")
        }

        sb.append(" $name")
        sb.append(" (").append(columns.joinToString()).append(")")

        return sb.toString()
    }
}
