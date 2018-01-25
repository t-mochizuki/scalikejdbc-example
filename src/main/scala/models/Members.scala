package models

import scalikejdbc._
import java.time.{LocalDate, ZonedDateTime}

case class Members(
  id: Int,
  name: String,
  description: Option[String] = None,
  birthday: Option[LocalDate] = None,
  createdAt: ZonedDateTime) {

  def save()(implicit session: DBSession = Members.autoSession): Members = Members.save(this)(session)

  def destroy()(implicit session: DBSession = Members.autoSession): Int = Members.destroy(this)(session)

}


object Members extends SQLSyntaxSupport[Members] {

  override val tableName = "MEMBERS"

  override val columns = Seq("ID", "NAME", "DESCRIPTION", "BIRTHDAY", "CREATED_AT")

  def apply(m: SyntaxProvider[Members])(rs: WrappedResultSet): Members = apply(m.resultName)(rs)
  def apply(m: ResultName[Members])(rs: WrappedResultSet): Members = new Members(
    id = rs.get(m.id),
    name = rs.get(m.name),
    description = rs.get(m.description),
    birthday = rs.get(m.birthday),
    createdAt = rs.get(m.createdAt)
  )

  val m = Members.syntax("m")

  override val autoSession = AutoSession

  def find(id: Int)(implicit session: DBSession = autoSession): Option[Members] = {
    withSQL {
      select.from(Members as m).where.eq(m.id, id)
    }.map(Members(m.resultName)).single.apply()
  }

  def findAll()(implicit session: DBSession = autoSession): List[Members] = {
    withSQL(select.from(Members as m)).map(Members(m.resultName)).list.apply()
  }

  def countAll()(implicit session: DBSession = autoSession): Long = {
    withSQL(select(sqls.count).from(Members as m)).map(rs => rs.long(1)).single.apply().get
  }

  def findBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Option[Members] = {
    withSQL {
      select.from(Members as m).where.append(where)
    }.map(Members(m.resultName)).single.apply()
  }

  def findAllBy(where: SQLSyntax)(implicit session: DBSession = autoSession): List[Members] = {
    withSQL {
      select.from(Members as m).where.append(where)
    }.map(Members(m.resultName)).list.apply()
  }

  def countBy(where: SQLSyntax)(implicit session: DBSession = autoSession): Long = {
    withSQL {
      select(sqls.count).from(Members as m).where.append(where)
    }.map(_.long(1)).single.apply().get
  }

  def create(
    name: String,
    description: Option[String] = None,
    birthday: Option[LocalDate] = None,
    createdAt: ZonedDateTime)(implicit session: DBSession = autoSession): Members = {
    val generatedKey = withSQL {
      insert.into(Members).namedValues(
        column.name -> name,
        column.description -> description,
        column.birthday -> birthday,
        column.createdAt -> createdAt
      )
    }.updateAndReturnGeneratedKey.apply()

    Members(
      id = generatedKey.toInt,
      name = name,
      description = description,
      birthday = birthday,
      createdAt = createdAt)
  }

  def batchInsert(entities: Seq[Members])(implicit session: DBSession = autoSession): List[Int] = {
    val params: Seq[Seq[(Symbol, Any)]] = entities.map(entity =>
      Seq(
        'name -> entity.name,
        'description -> entity.description,
        'birthday -> entity.birthday,
        'createdAt -> entity.createdAt))
    SQL("""insert into MEMBERS(
      NAME,
      DESCRIPTION,
      BIRTHDAY,
      CREATED_AT
    ) values (
      {name},
      {description},
      {birthday},
      {createdAt}
    )""").batchByName(params: _*).apply[List]()
  }

  def save(entity: Members)(implicit session: DBSession = autoSession): Members = {
    withSQL {
      update(Members).set(
        column.id -> entity.id,
        column.name -> entity.name,
        column.description -> entity.description,
        column.birthday -> entity.birthday,
        column.createdAt -> entity.createdAt
      ).where.eq(column.id, entity.id)
    }.update.apply()
    entity
  }

  def destroy(entity: Members)(implicit session: DBSession = autoSession): Int = {
    withSQL { delete.from(Members).where.eq(column.id, entity.id) }.update.apply()
  }

}
