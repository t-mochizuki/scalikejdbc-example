import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Source}
import java.io.File
import java.nio.file.StandardOpenOption._

import scalikejdbc._
import scalikejdbc.config._
import scalikejdbc.streams._

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
  //         column.name -> s"taro$id",
  //         column.createdAt -> sqls.currentTimestamp)
  //     }.update.apply()
  //   }
  // }

  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = system.dispatcher

  val emp = Emp.syntax("emp")
  val databasePublisher: DatabasePublisher[Emp] = DB readOnlyStream {
    withSQL {
      selectFrom(Emp as emp)
    }.map { rs =>
      Emp(rs.get(emp.resultName.id), rs.get(emp.resultName.name), rs.get(emp.resultName.createdAt))
    }.iterator()
  }

  val source = Source.fromPublisher(databasePublisher)
  val outputFile = new File("taro.csv")
  val sink = FileIO.toPath(outputFile.toPath, options = Set(CREATE, WRITE, TRUNCATE_EXISTING))
  val runnableGraph = source.map(entity => s"${entity.name}\n").map(akka.util.ByteString(_)).to(sink)

  runnableGraph.run()

  println("Main end")

  // system.terminate()
}
