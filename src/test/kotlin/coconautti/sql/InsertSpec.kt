package coconautti.sql

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.BehaviorSpec

class InsertSpec : BehaviorSpec() {
    init {
        given("an insert statement with two columns") {
            val stmt = Database.insertInto("users") {
                columns("id", "name")
                values(1, "Peter")
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("INSERT INTO users (id, name) VALUES (?, ?)")
                }
            }
        }

        given("an insert statement with one column") {
            val stmt = Database.insertInto("users") {
                columns("name")
                values("Peter")
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("INSERT INTO users (name) VALUES (?)")
                }
            }
        }

        given("an insert statement for postgres") {
            val stmt = Database.insertInto("users") {
                columns("name")
                values("Peter")
            }
            `when`("extracting SQL") {
                val sql = stmt.toString(SQLDialect.POSTGRESQL)
                then("it should match expectation") {
                    sql.shouldEqual("INSERT INTO users (name) VALUES (?) RETURNING *")
                }
            }
        }
    }
}
