package coconautti.sql

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.BehaviorSpec

class UpdateSpec : BehaviorSpec() {
    init {
        given("an update statement with two columns") {
            val stmt = Database.update("users") {
                set("firstName", "Peter")
                set("age", 42)
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("UPDATE users SET firstName = ?, age = ?")
                }
            }
        }

        given("an update statement with two columns and where clause") {
            val stmt = Database.update("users") {
                set("firstName", "Peter")
                set("age", 42)
                where("email".eq("peter@coconautti.com"))
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("UPDATE users SET firstName = ?, age = ? WHERE email = ?")
                }
            }
        }

        given("an update statement with two columns and complex where clause") {
            val stmt = Database.update("users") {
                set("firstName", "Peter")
                set("age", 42)
                where(("email" eq "peter@coconautti.com") and ("lastName" eq "Piper" ))
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("UPDATE users SET firstName = ?, age = ? WHERE email = ? AND lastName = ?")
                }
            }
        }
    }
}
