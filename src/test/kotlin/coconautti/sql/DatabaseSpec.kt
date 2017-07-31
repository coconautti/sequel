package coconautti.sql

import io.kotlintest.matchers.*
import io.kotlintest.specs.BehaviorSpec
import java.sql.DriverManager

class DatabaseSpec : BehaviorSpec() {

    init {
        val conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        conn.use {
            val stmt = conn.createStatement()
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY, name VARCHAR(32) NOT NULL)")
        }

        given("a database") {
            `when`("disconnected") {
                then("illegal state exception should be thrown") {
                    val stmt = Database.disconnect().insertInto("users") {
                        columns("name")
                        values("Peter")
                    }
                    shouldThrow<IllegalStateException> {
                        stmt.execute()
                    }
                }
            }

            `when`("connected") {
                then("insert record") {
                    Database.connect("jdbc:h2:mem:test").insertInto("users") {
                        columns("id", "name")
                        values(1, "Peter")
                    }.execute()
                }
            }

            `when`("making select query") {
                then("a two value record set should be returned") {
                    val stmt = Database.connect("jdbc:h2:mem:test").selectFrom("users") {
                        columns("id", "name")
                        where("id".eq(1))
                    }
                    val record = stmt.query().first()
                    record.size().shouldEqual(2)

                    val (id, name) = record as Record2
                    id as Long shouldEqual(1L)
                    name as String shouldEqual("Peter")
                }
            }

            `when`("making an insert") {
                then("no errors") {
                    Database.connect("jdbc:h2:mem:test").insertInto("users") {
                        columns("id", "name")
                        values(2, "Donald")
                    }.execute()
                }
            }

            `when`("transaction is successful") {
                then("rollback should not be called") {
                    Database.connect("jdbc:h2:mem:test").transaction {
                        insertInto("users") {
                            columns("id", "name")
                            values(10, "Alice")
                        }
                        insertInto("users") {
                            columns("id", "name")
                            values(11, "Bob")
                        }
                        insertInto("users") {
                            columns("id", "name")
                            values(12, "Charlie")
                        }
                        rollback { cause ->
                            fail(cause)
                        }
                    }.execute()
                }
            }

            `when`("transaction fails") {
                then("rollback should be called") {
                    Database.connect("jdbc:h2:mem:test").transaction {
                        insertInto("users") {
                            columns("id", "name")
                            values(20, "Alice")
                        }
                        insertInto("user") { // <- Typo in table name causes rollback
                            columns("id", "name")
                            values(21, "Bob")
                        }
                        insertInto("users") {
                            columns("id", "name")
                            values(22, "Charlie")
                        }
                        rollback { cause ->
                            cause.should { it.startsWith("Table \"USER\" not found") }
                        }
                    }.execute()
                }
            }

            `when`("batch insert") {
                then("no errors") {
                    Database.connect("jdbc:h2:mem:test").batchInsertInto("users") {
                        columns("id", "name")
                        values(30, "Alice")
                        values(31, "Bob")
                        values(32, "Charlie")
                    }.execute()
                }
            }
        }
    }
}
