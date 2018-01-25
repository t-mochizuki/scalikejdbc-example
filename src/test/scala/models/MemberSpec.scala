package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{LocalDate, ZonedDateTime}


class MemberSpec extends Specification {

  "Member" should {

    val m = Member.syntax("m")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Member.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Member.findBy(sqls.eq(m.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Member.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Member.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Member.findAllBy(sqls.eq(m.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Member.countBy(sqls.eq(m.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Member.create(name = "MyString", createdAt = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Member.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Member.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Member.findAll().head
      val deleted = Member.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Member.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Member.findAll()
      entities.foreach(e => Member.destroy(e))
      val batchInserted = Member.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
