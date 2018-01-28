The following uses java time instead of joda time.

``` scala
import scalikejdbc._
import scalikejdbc.config._
import java.time.ZonedDateTime

DBs.setup()
implicit val session = AutoSession

sql"""
create table if not exists member (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
""".execute.apply()

case class Member(id: Long, name: String, createdAt: java.time.ZonedDateTime)
object Member extends SQLSyntaxSupport[Member]

withSQL {
  insert.into(Member).values(1, "Alice", ZonedDateTime.now)
}.update.apply()
```

``` scala
import scalikejdbc._
import scalikejdbc.config._
import java.time.ZonedDateTime

DBs.setup()
implicit val session = AutoSession

sql"""
create table if not exists member (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
""".execute.apply()

case class Member(id: Long, name: String, createdAt: java.time.ZonedDateTime)
object Member extends SQLSyntaxSupport[Member]

withSQL {
  val m = Member.column
  insert.into(Member).namedValues(
    m.id -> 2,
    m.name -> "Bob",
    m.createdAt -> ZonedDateTime.now
  )
}.update.apply()
```
