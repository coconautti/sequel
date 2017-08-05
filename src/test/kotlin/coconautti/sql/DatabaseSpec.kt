package coconautti.sql

import io.kotlintest.TestCaseContext
import io.kotlintest.matchers.*
import io.kotlintest.specs.FunSpec

class DatabaseSpec : FunSpec() {

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        Database.connect("jdbc:h2:mem:test")
        Database.createTable("users", false) {
            bigint("id").primaryKey()
            varchar("name", 32)
        }

        test()

        Database.dropTable("users", false)
    }

    init {

        test("throw illegal state exception of not connected") {
            Database.disconnect()
            val stmt = Database.disconnect().insertInto("users") {
                columns("name")
                values("Peter")
            }
            shouldThrow<IllegalStateException> {
                stmt.execute()
            }
            Database.connect("jdbc:h2:mem:test")
        }

        test("select query returns a two-value record") {
            Database.insertInto("users") {
                columns("id", "name")
                values(1, "Peter")
            }.execute()

            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                where("id".eq(1))
            }
            val record = stmt.query().first()
            record.size().shouldEqual(2)

            val (id, name) = record as Record2
            id as Long shouldEqual(1L)
            name as String shouldEqual("Peter")
        }

        test("successful transaction shouldn't call rollback") {
            Database.transaction {
                insertInto("users") {
                    columns("id", "name")
                    values(1, "Alice")
                }
                insertInto("users") {
                    columns("id", "name")
                    values(2, "Bob")
                }
                insertInto("users") {
                    columns("id", "name")
                    values(3, "Charlie")
                }
                rollback { cause ->
                    fail(cause)
                }
            }.execute()
        }

        test("failed transaction should call rollback") {
            Database.transaction {
                insertInto("users") {
                    columns("id", "name")
                    values(1, "Alice")
                }
                insertInto("user") { // <- Typo in table name causes rollback
                    columns("id", "name")
                    values(2, "Bob")
                }
                insertInto("users") {
                    columns("id", "name")
                    values(3, "Charlie")
                }
                rollback { cause ->
                    cause.should { it.startsWith("Table \"USER\" not found") }
                }
            }.execute()
        }

        test("batch insert executes without errors") {
            Database.batchInsertInto("users") {
                columns("id", "name")
                values(1, "Alice")
                values(2, "Bob")
                values(3, "Charlie")
            }.execute()
        }
    }
}
