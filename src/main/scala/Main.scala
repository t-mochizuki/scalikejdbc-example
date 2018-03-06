import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import java.io.{File, FileOutputStream}

import org.apache.poi.xssf.streaming.SXSSFWorkbook
import scalikejdbc._
import scalikejdbc.config._
import scalikejdbc.streams._

case class Emp(
  id: Long,
  name: String,
  createdAt: java.time.ZonedDateTime
)

object Emp extends SQLSyntaxSupport[Emp]

// JAVA_OPTS="-Xmx100M" sbt run
object Main extends App {

  println("Main start")

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = true,
    singleLineMode = true,
    printUnprocessedStackTrace = false,
    stackTraceDepth= 15,
    logLevel = 'debug,
    warningEnabled = true,
    warningThresholdMillis = 3000L,
    warningLogLevel = 'warn
  )

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
  // 1 to 500000 foreach { id =>
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
  val runnableGraph = source.zipWithIndex

  val workbook = new SXSSFWorkbook
  val sheet = workbook.createSheet("test1")

  runnableGraph
    .runForeach {
      case (entity, rowIndex) => sheet.createRow(rowIndex.toInt).createCell(0).setCellValue(entity.name)
    }
    .andThen {
      case _ => {
        val output = File.createTempFile("taka", ".xlsx")
        println(output.getPath)
        val stream = new FileOutputStream(output)
        workbook.write(stream)

        system.terminate
      }
    }


  println("Main end")

  // system.terminate()
}
