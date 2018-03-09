package example

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import java.io.{File, FileOutputStream}

import org.apache.poi.xssf.streaming.SXSSFWorkbook
// import org.apache.poi.xssf.usermodel.XSSFWorkbook
import scalikejdbc._
import scalikejdbc.config._
import scalikejdbc.streams._

// JAVA_OPTS="-Xmx200M" sbt run
object Main extends App {

  println("Main start")

  DBs.setup()

  implicit val system = ActorSystem("system")
  implicit val materializer = ActorMaterializer()
  implicit val dispatcher = system.dispatcher

  // source
  val databasePublisher: DatabasePublisher[Emp] = DB readOnlyStream {
    val emp = Emp.syntax("emp")
    withSQL {
      selectFrom(Emp as emp)
    }.map { rs =>
      Emp(rs.get(emp.resultName.id), rs.get(emp.resultName.name), rs.get(emp.resultName.createdAt))
    }.iterator()
  }
  val source: Source[Emp, NotUsed] = Source.fromPublisher(databasePublisher)

  // POI-SXSSF
  val workbook = new SXSSFWorkbook
  // POI-XSSF
  // val workbook = new XSSFWorkbook
  val sheet = workbook.createSheet("test1")

  source
    .zipWithIndex
    .runForeach {
      case (entity, rowIndex) =>
        val row = sheet.createRow(rowIndex.toInt)
        row.createCell(0).setCellValue(entity.id)
        row.createCell(1).setCellValue(entity.name)
        row.createCell(2).setCellValue(entity.createdAt.toString)
    }
    .andThen {
      case _ => {
        val output = File.createTempFile("taro", ".xlsx")
        println(s"File path is ${output.getPath}")
        val stream = new FileOutputStream(output)
        workbook.write(stream)

        stream.close
        system.terminate
      }
    }

  println("Main end")
}
