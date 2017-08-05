package coconautti.ddl

import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.FunSpec

class CreateTableSpec : FunSpec({

    test("create table") {
        val stmt = CreateTable("users")
        stmt.bigint("id").primaryKey().autoIncrement()
        stmt.varchar("firstName", 32)
        stmt.varchar("lastName", 64)
        stmt.varchar("email", 128)
        stmt.toString().shouldEqual("CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, firstName VARCHAR(32) NOT NULL, lastName VARCHAR(64) NOT NULL, email VARCHAR(128) NOT NULL)")
    }

    test("create table w/ nullable field") {
        val stmt = CreateTable("emails")
        stmt.varchar("username", 32).primaryKey()
        stmt.varchar("email", 128).nullable()
        stmt.toString().shouldEqual("CREATE TABLE emails (username VARCHAR(32) PRIMARY KEY, email VARCHAR(128))")
    }

    test("create table w/ unique field") {
        val stmt = CreateTable("emails")
        stmt.varchar("username", 32).primaryKey()
        stmt.varchar("email", 128).unique()
        stmt.toString().shouldEqual("CREATE TABLE emails (username VARCHAR(32) PRIMARY KEY, email VARCHAR(128) UNIQUE NOT NULL)")
    }

    test("create table w/o force") {
        val stmt = CreateTable("foo", false)
        stmt.varchar("bar", 32).nullable()
        stmt.toString().shouldEqual("CREATE TABLE IF NOT EXISTS foo (bar VARCHAR(32))")
    }

    test("create table with default value") {
        val stmt = CreateTable("emails")
        stmt.varchar("email", 128).primaryKey()
        stmt.boolean("verified").default(false)
        stmt.toString().shouldEqual("CREATE TABLE emails (email VARCHAR(128) PRIMARY KEY, verified BOOLEAN NOT NULL DEFAULT FALSE)")
    }

    test("create table with timestamp") {
        val stmt = CreateTable("logins")
        stmt.bigint("id").primaryKey()
        stmt.timestamp("date")
        stmt.toString().shouldEqual("CREATE TABLE logins (id BIGINT PRIMARY KEY, date TIMESTAMP NOT NULL)")
    }

    test("create table with clob") {
        val stmt = CreateTable("dump")
        stmt.clob("data")
        stmt.toString().shouldEqual("CREATE TABLE dump (data CLOB NOT NULL)")
    }

    test("create table with function as default value") {
        val stmt = CreateTable("logins")
        stmt.bigint("id").primaryKey()
        stmt.timestamp("date").default("NOW()")
        stmt.toString().shouldEqual("CREATE TABLE logins (id BIGINT PRIMARY KEY, date TIMESTAMP NOT NULL DEFAULT NOW())")
    }
})
