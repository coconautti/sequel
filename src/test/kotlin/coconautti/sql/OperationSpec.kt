package coconautti.sql

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec

class OperationSpec : FunSpec({

    test("and expression using longs") {
        val op = ("age" lte 42) and ("shoeSize" gte 42)
        op.toString().shouldEqual("age <= ? AND shoeSize >= ?")
    }

    test("and expression using strings") {
        val op = ("firstName" eq "Donald") and ("lastName" ne "Trump")
        op.toString().shouldEqual("firstName = ? AND lastName != ?")
    }

    test("and expression using values") {
        val op = ("age" lt Value(32)) and ("shoeSize" lte Value(43))
        op.toString().shouldEqual("age < ? AND shoeSize <= ?")
    }

    test("or expression using longs") {
        val op = ("age" lt 42) or ("shoeSize" gt 42)
        op.toString().shouldEqual("age < ? OR shoeSize > ?")
    }

    test("or expression using strings") {
        val op = ("age" lt 32) or ("shoeSize" lte 45)
        op.toString().shouldEqual("age < ? OR shoeSize <= ?")
    }

    test("or expression using values") {
        val op = ("age" lte Value(42)) or ("shoeSize" gt Value(39))
        op.toString().shouldEqual("age <= ? OR shoeSize > ?")
    }
})
