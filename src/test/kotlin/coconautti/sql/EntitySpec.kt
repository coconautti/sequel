package coconautti.sql

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.BehaviorSpec
import java.sql.DriverManager

data class User(val id: Long, val name: String) {
    companion object {
        fun fromRecord(record: Record): User {
            val (id, name) = record as Record2
            return (User(id as Long, name as String))
        }
    }
}

class EntitySpec : BehaviorSpec() {

    init {
        val conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1")
        conn.use {
            val stmt = conn.createStatement()
            stmt.execute("DROP TABLE IF EXISTS users")
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id BIGINT PRIMARY KEY, name VARCHAR(32) NOT NULL)")
        }

        given("a user") {
            val user = User(1, "Peter")
            `when`("retrieving") {
                then("should be able to construct user back") {
                    Database.connect("jdbc:h2:mem:test").insertInto("users") {
                        columns("id", "name")
                        values(user.id, user.name)
                    }.execute()

                    val record = Database.connect("jdbc:h2:mem:test").selectFrom("users") {
                        columns("id", "name")
                    }.query().first()
                    val retrievedUser = User.fromRecord(record)
                    retrievedUser.id.shouldEqual(1L)
                    retrievedUser.name.shouldEqual("Peter")
                }

            }
        }
    }
}
