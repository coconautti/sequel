# sequel

[![Build Status](https://travis-ci.org/coconautti/sequel.svg?branch=master)](https://travis-ci.org/coconautti/sequel)

A lightweight SQL query DSL for Kotlin. The library is in an early stage of development, so don't expect the DSL to support anything fancy.

sequel has been tested with the following databases:

* H2
* PostgreSQL
 
# Getting started

sequel is currently available in source code format only, thus you need to build the jar and drop it into your project and include it from the project build tool. Include the jar produced by `gradle jar` into your parent project or reference the project from parent project if using Gradle.

# Queries

Before running any queries, make sure to connect to the database:

```kotlin
Database.connect("jdbc:h2:mem:test")
```

Failing to connect the database before running queries will result in an `IllegalStateException`.

Inserting a record (assuming the table exists):

```kotlin
val id = Database.insertInto("users") {
    columns("id", "name")
    values(1, "Peter")
}.execute() as Long
```

In case of an insert, `execute()` returns the generated id (supports only the first generated key).

Updating a record can be done using `execute()`. In this case the method returns null, as there are no generated keys to be returned. 

```kotlin
Database.update("users") {
    set("name", "Piper")
    where("id" eq 1)
}.execute()
```

Fetching a record and constructing a domain object out of it can be done using `query()`. Note that `query()` needs to process the entire JDBC result set in order to build the record set, thus try to avoid it with larger result sets. 

```kotlin
data class User(val id: Long, val name: String) {
    companion object {
        fun fromRecord(record: Record): User {
            val (id, name) = record as Record2
            return User(id as Long, name as String)        
        }
    }
}

val user = Database.selectFrom("users") {
    columns("id", "name")
    where("id" eq 1)
}.query().map { User.fromRecord(record) }.first()

```

Alternatively, you can use `fetch()` to return results directly into a data class and thus reduce the amount of boilerplate. However, do note that `fetch()` uses reflection, so beware of potential performance degradation when using it with large result sets. 

```kotlin
data class User(val id: Long, val name: String)

val user = Database.selectFrom("users") {
    columns("id", "name")
    where("id" eq 1)
}.fetch().first()

```

Deleting a record:

```kotlin
Database.deleteFrom("users") {
    where("id" eq 1)
}.execute()
```

You can do transactions too. The rollback closure will be called in case the transaction fails. `rollback` receives the `cause` of the rollback as an error string.

```kotlin
Database.transaction {
    insertInto("users") {
        columns("id", "name")
        values(1, "Alice")
    }
    insertInto("user") { // <- Typo in table name causes rollback
        columns("id", "name")
        values(2, "Bob")
    }
    insertInto("users") {
        columns("id", "name")
        values(3, "Charlie")
    }
    rollback { cause ->
        // Handle error
    }
}.execute()
```

Batch inserts can be done using `batchInsertInto()`:

```kotlin
Database.batchInsertInto("users") {
    columns("id", "name")
    values(1, "Alice")
    values(2, "Bob")
    values(3, "Charlie")
}.execute()
```

Just as with `insertInto()`, `batchInserInto()` will return a list of generated keys (same limitations apply).

# Data Definition Language (DDL)

sequel has a very limited support for creating and dropping tables, mainly to facilitate testing. SQL data types will be added along the way.

All currently supported SQL data types:

* bigint
* bigserial
* boolean
* clob (*)
* jsonb
* timestamp (**)
* varchar

(*) sequel doesn't have a special type for clobs. However, `String`s will be saved automatically as clobs and clobs will be read in as `String`s.
(**) sequel supports JodaTime's DateTime but with a catch. For Records, i.e. when using `query()` you'll get a `java.sql.TimeStamp` as the value type. When using `fetch()` timestamps will be returned as `DateTime`s. This discrepancy will hopefully change in the near future.

Create a table with:

```kotlin
Database.createTable("users") {
    bigint("id").primaryKey().autoIncrement()
    varchar("name")
    varchar("email").unique()
    varchar("description").nullable()
    timestamp("created").default("CURRENT_TIMESTAMP()")
}
```

`createTable` is executed automatically once the statement is complete, i.e. it differs from the queries in the sense that no `execute()` etc. methods need to be called.

Dropping a table is easy too:

```kotlin
Database.dropTable("users")
```

As with `createTable`, `dropTable` is executed without any other function calls.



# Todo

- [X] Add CI for PRs
- [X] Add support for creating and dropping tables
- [X] Release 0.2.0
- [X] Add support for bigserial & jsonb
- [X] Add support for SQL dialects
- [ ] Add support for joins
- [ ] Release 0.3.0
