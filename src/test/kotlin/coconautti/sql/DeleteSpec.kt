package coconautti.sql

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.BehaviorSpec

class DeleteSpec : BehaviorSpec() {
    init {
        given("a delete statement") {
            val stmt = Database.deleteFrom("users") {}
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("DELETE FROM users")
                }
            }
        }

        given("a delete statement with where clause") {
            val stmt = Database.deleteFrom("users") {
                where("id" eq 1)
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("DELETE FROM users WHERE id = ?")
                }
            }
        }

        given("a delete statement with composite where clause") {
            val stmt = Database.deleteFrom("users") {
                where(("id" eq 1) or ("name" ne "Donald"))
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("DELETE FROM users WHERE id = ? OR name != ?")
                }
            }
        }
    }
}
