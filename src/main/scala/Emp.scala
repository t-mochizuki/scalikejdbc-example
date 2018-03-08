package example

import scalikejdbc._

case class Emp(
  id: Long,
  name: String,
  createdAt: java.time.ZonedDateTime
)

object Emp extends SQLSyntaxSupport[Emp]

