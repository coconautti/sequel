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

        given("a select statement with order by") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                orderBy("name")
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users ORDER BY name")
                }
            }
        }

        given("a select statement with order by desc") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                orderBy("name").desc()
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users ORDER BY name DESC")
                }
            }
        }

        given("a select statement with order by asc") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                orderBy("name").asc()
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users ORDER BY name ASC")
                }
            }
        }

        given("a select statement with order by and limit") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                orderBy("name")
                limit(2)
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users ORDER BY name LIMIT 2")
                }
            }
        }

        given("a select statement with offset and fetch next") {
            val stmt = Database.selectFrom("users") {
                columns("id", "name")
                orderBy("name")
                offset(10)
                fetchNext(5)
            }
            `when`("extracting SQL") {
                val sql = stmt.toString()
                then("it should match expectation") {
                    sql.shouldEqual("SELECT id, name FROM users ORDER BY name OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY")
                }
            }
        }
    }
}
