package coconautti.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.PreparedStatement
import kotlin.reflect.KClass

object Database {
    private val log = LoggerFactory.getLogger(Database::class.java)
    private var datasource: HikariDataSource? = null

    fun connect(url: String, username: String? = null, password: String? = null): Database {
        val config = HikariConfig()
        config.jdbcUrl = url
        config.username = username
        config.password = password
        datasource = HikariDataSource(config)
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

    internal fun connection(): Connection {
        val ds = datasource
        ds ?: throw IllegalStateException("Database hasn't been connected")
        return ds.connection
    }

    private fun prepareStatement(conn: Connection, statement: Statement): PreparedStatement {
        log.debug("Preparing statement: $statement")
        log.debug("with values: ${statement.values().joinToString()}")

        val stmt = conn.prepareStatement(statement.toString())
        for (index in statement.values().indices) {
            stmt.setObject(index + 1, statement.values()[index].value)
        }

        return stmt
    }

    internal fun execute(conn: Connection, statement: Statement): Any? {
        val stmt = prepareStatement(conn, statement)
        stmt.execute()

        val generatedKeys = stmt.generatedKeys
        if (generatedKeys.next()) {
            return generatedKeys.getObject(1)
        } else {
            return null
        }
    }

    internal fun execute(statement: Statement): Any? {
        val conn = connection()
        conn.use {
            return execute(conn, statement)
        }
    }

    private fun prepareBatchStatement(stmt: PreparedStatement, values: List<Value>) {
        log.debug("Preparing batch statement with values: ${values.joinToString()}")

        for (index in values.indices) {
            stmt.setObject(index + 1, values[index].value)
        }
        stmt.addBatch()
    }

    internal fun execute(conn: Connection, statement: BatchStatement): List<Any> {
        try {
            conn.autoCommit = false
            val stmt = conn.prepareStatement(statement.toString())
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
            return results
        } finally {
            conn.autoCommit = true
        }
    }

    internal fun execute(statement: BatchStatement): List<Any> {
        val conn = connection()
        conn.use {
            return execute(conn, statement)
        }
    }

    internal fun query(query: Query): List<Record> {
        val conn = connection()
        conn.use {
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
            return records
        }
    }

    internal fun <T> fetch(query: Query, klass: KClass<*>): List<T> {
        val conn = connection()
        conn.use {
            val stmt = prepareStatement(conn, query)
            val rs = stmt.executeQuery()

            @Suppress("UNUSED_VARIABLE")
            val ctor = klass.constructors.first()

            val objects = ArrayList<T>()
            while (rs.next()) {
                val values = query.columns().map { rs.getObject(it.toString()) }.toTypedArray()
                val obj = ctor.call(*values)
                @Suppress("UNCHECKED_CAST")
                objects.add(obj as T)
            }
            return objects
        }
    }
}