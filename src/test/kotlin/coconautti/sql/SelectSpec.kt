package coconautti.sql

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.BehaviorSpec

class SelectSpec : BehaviorSpec() {
    init {
        given("a select statement with two columns and no where clause") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users")
                }
            }
        }

        given("a select statement with one column and no where clause") {
            val stmt = Database.selectFrom("users") {
                columns("name")
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT name FROM users")
                }
            }
        }

        given("a select statement with no columns and no where clause") {
            val stmt = Database.selectFrom("users") {}
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT * FROM users")
                }
            }
        }

        given("a select statement with where clause") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                where("id".eq(1))
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users WHERE id = ?")
                }
            }
        }

        given("a select statement with composite where clause") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                where(("id" eq 1) and ("name" eq "Donald"))
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users WHERE id = ? AND name = ?")
                }
            }
        }
    }
}
