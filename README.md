The following has java.time.ZonedDateTime instead of org.joda.time.DateTime.

On sbt console I checked no error.

``` scala
import scalikejdbc._
import java.time.ZonedDateTime

class HugeTable(
  val column1: Long,
  val column2: Option[String],
  val column3: String,
  val column4: Int,
  val column5: ZonedDateTime,
  val column6: Int,
  val column7: Int,
  val column8: Int,
  val column9: Int,
  val column10: Int,
  val column11: Int,
  val column12: Int,
  val column13: Int,
  val column14: Int,
  val column15: Int,
  val column16: Int,
  val column17: Int,
  val column18: Int,
  val column19: Int,
  val column20: Int,
  val column21: Int,
  val column22: Int,
  val column23: ZonedDateTime) extends EntityEquality {

  override val entityIdentity = Seq(
    column1,
    column2,
    column3,
    column4,
    column5,
    column6,
    column7,
    column8,
    column9,
    column10,
    column11,
    column12,
    column13,
    column14,
    column15,
    column16,
    column17,
    column18,
    column19,
    column20,
    column21,
    column22,
    column23).mkString("\t")

}
```
