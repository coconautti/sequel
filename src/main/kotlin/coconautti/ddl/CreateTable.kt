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

abstract class Column(val name: String) {
    private var primaryKey = false
    private var autoIncrement = false
    private var unique = false
    private var nullable = false

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

        return sb.toString()
    }
}

class Varchar(name: String, private val length: Int) : Column(name) {
    override fun type(): String = " VARCHAR($length)"
}

class Bigint(name: String) : Column(name) {
    override fun type(): String = " BIGINT"
}
