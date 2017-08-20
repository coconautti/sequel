package coconautti.sql

import io.kotlintest.TestCaseContext
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.BehaviorSpec

data class User(val id: Long, val name: String) {
    companion object {
        fun fromRecord(record: Record): User {
            val (id, name) = record as Record2
            return (User(id as Long, name as String))
        }
    }
}

class EntitySpec : BehaviorSpec() {

    override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
        Database.connect("jdbc:h2:mem:test")
        Database.createTable("users") {
            bigint("id").primaryKey()
            varchar("name", 32)
        }

        test()

        Database.dropTable("users")
    }

    init {
        given("a user") {
            val user = User(1, "Peter")
            `when`("retrieving") {
                then("should be able to construct user back") {
                    Database.insertInto("users") {
                        columns("id", "name")
                        values(user.id, user.name)
                    }.execute()

                    val retrievedUser = Database.selectFrom("users") {
                        columns("id", "name")
                    }.query().map { User.fromRecord(it) }.first()
                    retrievedUser.id.shouldEqual(1L)
                    retrievedUser.name.shouldEqual("Peter")
                }

            }
        }
    }
}
