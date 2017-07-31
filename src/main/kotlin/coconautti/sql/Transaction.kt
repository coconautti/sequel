package coconautti.sql

import org.slf4j.LoggerFactory
import java.sql.SQLException

class Transaction(val database: Database) {
    private val log = LoggerFactory.getLogger(Transaction::class.java)
    private val statements = ArrayList<Statement>()
    private var rollback: ((String) -> Unit)? = null

    fun insertInto(table: String, init: Insert.() -> Unit): Insert {
        val insert = Insert(database, table)
        insert.init()
        statements.add(insert)
        return insert
    }

    fun selectFrom(table: String, init: Select.() -> Unit): Select {
        val select = Select(database, table)
        select.init()
        statements.add(select)
        return select
    }

    fun deleteFrom(table: String, init: Delete.() -> Unit): Delete {
        val delete = Delete(database, table)
        delete.init()
        statements.add(delete)
        return delete
    }

    fun update(table: String, init: Update.() -> Unit): Update {
        val update = Update(database, table)
        update.init()
        statements.add(update)
        return update
    }

    fun execute() {
        val conn = database.connection()
        conn.use {
            try {
                log.debug("Start of transaction")
                conn.autoCommit = false
                statements.forEach { it.execute(conn) }
                conn.commit()
            } catch (e: SQLException) {
                log.debug("Transaction failed", e)
                conn.rollback()
                rollback?.invoke(e.message ?: "unknown")
            } finally {
                conn.autoCommit = true
                log.debug("End of transaction")
            }
        }
    }

    fun rollback(handler: (String) -> Unit): Transaction {
        rollback = handler
        return this
    }
}
