import scalikejdbc._
import scalikejdbc.config._

case class Emp(
  id: Long,
  name: String,
  createdAt: java.time.ZonedDateTime
)

object Emp extends SQLSyntaxSupport[Emp]

// JAVA_OPTS="-Xmx80M" sbt run
object Main extends App {

  println("Main start")

  DBs.setup()

  // DB localTx { implicit session =>
  //   sql"""
  //   create table emp (
  //     id serial not null primary key,
  //     name varchar(64) not null,
  //     created_at timestamp not null
  //   )
  //   """.execute.apply()
  // }
  //
  // val column = Emp.column
  // 1 to 200000 foreach { id =>
  //   DB localTx { implicit s =>
  //     withSQL {
  //       insert.into(Emp).namedValues(
  //         column.id -> id,
  //         column.name -> s"taka$id",
  //         column.createdAt -> sqls.currentTimestamp)
  //     }.update.apply()
  //   }
  // }


  val emp = Emp.syntax("emp")
  val result = DB localTx { implicit s =>
    withSQL {
      selectFrom(Emp as emp)
    }.map { rs =>
      Emp(rs.get(emp.resultName.id), rs.get(emp.resultName.name), rs.get(emp.resultName.createdAt))
    }.list.apply()
  }

  println("Main end")
}
