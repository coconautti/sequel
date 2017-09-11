package coconautti.sql

import coconautti.ddl.CreateTable
import coconautti.ddl.DropTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.LoggerFactory
import java.sql.Clob
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import kotlin.reflect.KClass

enum class SQLDialect {
    GENERIC, H2, POSTGRESQL
}

object Database {
    private val log = LoggerFactory.getLogger(Database::class.java)
    private var datasource: HikariDataSource? = null
    private var dialect = SQLDialect.GENERIC

    fun connect(url: String, username: String? = null, password: String? = null): Database {
        val config = HikariConfig()
        config.jdbcUrl = url
        config.username = username
        config.password = password
        datasource = HikariDataSource(config)

        if (url.startsWith("jdbc:h2:")) {
            dialect = SQLDialect.H2
        } else if (url.startsWith("jdbc:postgresql:")) {
            dialect = SQLDialect.POSTGRESQL
        }

        return this
    }

    fun disconnect(): Database {
        datasource = null
        return this
    }

    fun insertInto(table: String, init: Insert.() -> Unit): Insert {
        val insert = Insert(this, table)
        insert.init()
        return insert
    }

    fun selectFrom(table: String, init: Select.() -> Unit): Select {
        val select = Select(this, table)
        select.init()
        return select
    }

    fun deleteFrom(table: String, init: Delete.() -> Unit): Delete {
        val delete = Delete(this, table)
        delete.init()
        return delete
    }

    fun update(table: String, init: Update.() -> Unit): Update {
        val update = Update(this, table)
        update.init()
        return update
    }

    fun transaction(init: Transaction.() -> Unit): Transaction {
        val transaction = Transaction(this)
        transaction.init()
        return transaction
    }

    fun batchInsertInto(table: String, init: BatchInsert.() -> Unit): BatchInsert {
        val batchInsert = BatchInsert(this, table)
        batchInsert.init()
        return batchInsert
    }

    fun createTable(name: String, force: Boolean = true, init: CreateTable.() -> Unit) {
        val createTable = CreateTable(name, force)
        createTable.init()

        val conn = connection()
        conn.use {
            val stmt = conn.prepareStatement(createTable.toString())
            stmt.execute()
        }
    }

    fun dropTable(name: String, force: Boolean = true) {
        val dropTable = DropTable(name, force)
        val conn = connection()
        conn.use {
            val stmt = conn.prepareStatement(dropTable.toString())
            stmt.execute()
        }
    }

    internal fun connection(): Connection {
        val ds = datasource
        ds ?: throw IllegalStateException("Database hasn't been connected")
        return ds.connection
    }

    private fun prepareStatement(conn: Connection, statement: Statement): PreparedStatement {
        log.debug("Preparing statement: ${statement.toString(dialect)}")
        log.debug("with values: ${statement.values().joinToString()}")

        // Unwrap and convert values to SQL data types
        val values = statement.values().map { value ->
            if (value.value is DateTime) {
                Timestamp.valueOf(value.value.toString("YYYY-MM-dd HH:mm:ss.SSS"))
            } else {
                value.value
            }
        }

        val stmt = conn.prepareStatement(statement.toString(dialect))
        values.indices.forEach { index ->
            stmt.setObject(index + 1, values[index])
        }
        return stmt
    }

    internal fun execute(conn: Connection, statement: Statement): Any? {
        val stmt = prepareStatement(conn, statement)
        stmt.execute()

        val generatedKeys = stmt.generatedKeys
        return if (generatedKeys.next()) generatedKeys.getObject(1) else null
    }

    internal fun execute(statement: Statement): Any? {
        val conn = connection()
        return conn.use {
            execute(conn, statement)
        }
    }

    private fun prepareBatchStatement(stmt: PreparedStatement, values: List<Value>) {
        log.debug("Preparing batch statement with values: ${values.joinToString()}")

        // Unwrap and convert values to SQL data types
        val convertedValues = values.map { value ->
            if (value.value is DateTime) {
                Timestamp.valueOf(value.value.toString("YYYY-MM-dd HH:mm:ss.SSS"))
            } else {
                value.value
            }
        }

        convertedValues.indices.forEach { index ->
            stmt.setObject(index + 1, convertedValues[index])
        }
        stmt.addBatch()
    }

    internal fun execute(conn: Connection, statement: BatchStatement): List<Any> {
        return try {
            conn.autoCommit = false
            val stmt = conn.prepareStatement(statement.toString(dialect))
            statement.values().forEach { values ->
                prepareBatchStatement(stmt, values)
            }
            stmt.executeBatch()
            conn.commit()

            val results = ArrayList<Any>()
            val rs = stmt.generatedKeys
            while (rs.next()) {
                results.add(rs.getObject(1))
            }
            results
        } finally {
            conn.autoCommit = true
        }
    }

    internal fun execute(statement: BatchStatement): List<Any> {
        val conn = connection()
        return conn.use {
            execute(conn, statement)
        }
    }

    internal fun query(query: Query): List<Record> {
        val conn = connection()
        return conn.use {
            val stmt = prepareStatement(conn, query)
            val rs = stmt.executeQuery()

            val records = ArrayList<Record>()
            while (rs.next()) {
                val record = when(rs.metaData.columnCount) {
                    1 -> Record1(rs.getObject(1))
                    2 -> Record2(rs.getObject(1), rs.getObject(2))
                    3 -> Record3(rs.getObject(1), rs.getObject(2), rs.getObject(3))
                    4 -> Record4(rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4))
                    5 -> Record5(rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4), rs.getObject(5))
                    6 -> Record6(rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4), rs.getObject(5), rs.getObject(6))
                    7 -> Record7(rs.getObject(1), rs.getObject(2), rs.getObject(3), rs.getObject(4), rs.getObject(5), rs.getObject(6), rs.getObject(7))
                    else -> throw IllegalStateException("Unable to handle 0 or 7+ result sets")
                }
                records.add(record)
            }
            records
        }
    }

    internal fun <T> fetch(query: Query, klass: KClass<*>): List<T> {
        val conn = connection()
        return conn.use {
            val stmt = prepareStatement(conn, query)
            val rs = stmt.executeQuery()

            @Suppress("UNUSED_VARIABLE")
            val ctor = klass.constructors.first()
            val formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss.SSS")

            val objects = ArrayList<T>()
            while (rs.next()) {
                val values = query
                        .columns()
                        .map { rs.getObject(it.toString()) }
                        .map { value ->
                            if (value is Clob) {
                                value.getSubString(1, value.length().toInt())
                            } else if (value is Timestamp) {
                                DateTime.parse(value.toString(), formatter)
                            } else {
                                value
                            }
                        }
                        .toTypedArray()
                println(values.joinToString())
                val obj = ctor.call(*values)
                @Suppress("UNCHECKED_CAST")
                objects.add(obj as T)
            }
            objects
        }
    }
}
