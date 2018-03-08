package example

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import java.io.File
import java.nio.file.StandardOpenOption._

import scalikejdbc._
import scalikejdbc.config._
import scalikejdbc.streams._

import scala.concurrent.Future

// JAVA_OPTS="-Xmx80M" sbt run
object Main extends App {

  println("Main start")

  DBs.setup()

  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = system.dispatcher

  // source
  val emp = Emp.syntax("emp")
  val databasePublisher: DatabasePublisher[Emp] = DB readOnlyStream {
    withSQL {
      selectFrom(Emp as emp)
    }.map { rs =>
      Emp(rs.get(emp.resultName.id), rs.get(emp.resultName.name), rs.get(emp.resultName.createdAt))
    }.iterator()
  }
  val source: Source[Emp, NotUsed] = Source.fromPublisher(databasePublisher)

  // sink
  val output = new File("taro.csv")
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(output.toPath, options = Set(CREATE, WRITE, APPEND))

  // run
  source
    .map(entity => s"${entity.id}, ${entity.name}, ${entity.createdAt}\n")
    .map(ByteString(_))
    .runWith(sink)
    .andThen {
      case _ => system.terminate
    }

  println("Main end")
}
