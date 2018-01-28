This case does not use joda time.

On sbt console I checked no error.

``` scala
import scalikejdbc._
import scalikejdbc.config._

DBs.setup()

DB localTx { implicit session =>
  sql"""
create table emp (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
  """.execute.apply()
}

val id = 1
val name = "Candle"
val newName = "Sandel"

DB localTx { implicit session =>
  sql"""insert into emp (id, name, created_at) values (${id}, ${name}, current_timestamp)"""
    .update.apply()
  val newId = sql"insert into emp (name, created_at) values (${name}, current_timestamp)"
    .updateAndReturnGeneratedKey.apply()
  sql"update emp set name = ${newName} where id = ${newId}".update.apply()
  sql"delete emp where id = ${newId}".update.apply()
}
```

``` scala
import scalikejdbc._
import scalikejdbc.config._

DBs.setup()

DB localTx { implicit session =>
  sql"""
create table emp (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
  """.execute.apply()
}

case class Emp(
  id: Long,
  name: Option[String],
  createdAt: java.time.ZonedDateTime
)

object Emp extends SQLSyntaxSupport[Emp]

val id = 1
val name = "Candle"
val newName = "Sandel"
val column = Emp.column
DB localTx { implicit s =>
  withSQL {
    insert.into(Emp).namedValues(
      column.id -> id,
      column.name -> name,
      column.createdAt -> sqls.currentTimestamp)
  }.update.apply()

  val newId: Long = withSQL {
    insert.into(Emp).namedValues(column.name -> name, column.createdAt -> sqls.currentTimestamp)
  }.updateAndReturnGeneratedKey.apply()

  withSQL { update(Emp).set(column.name -> newName).where.eq(column.id, newId) }.update.apply()
  withSQL { delete.from(Emp).where.eq(column.id, newId) }.update.apply()
}
```
