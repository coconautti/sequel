package coconautti.ddl

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec

class DropTableSpec : FunSpec({

    test("drop table") {
        val stmt = DropTable("users")
        stmt.toString().shouldEqual("DROP TABLE users")
    }

    test("drop table w/o force ") {
        val stmt = DropTable("users", false)
        stmt.toString().shouldEqual("DROP TABLE IF EXISTS users")
    }
})
