package models

import scalikejdbc.specs2.mutable.AutoRollback
import org.specs2.mutable._
import scalikejdbc._
import java.time.{LocalDate, ZonedDateTime}


class MembersSpec extends Specification {

  "Members" should {

    val m = Members.syntax("m")

    "find by primary keys" in new AutoRollback {
      val maybeFound = Members.find(123)
      maybeFound.isDefined should beTrue
    }
    "find by where clauses" in new AutoRollback {
      val maybeFound = Members.findBy(sqls.eq(m.id, 123))
      maybeFound.isDefined should beTrue
    }
    "find all records" in new AutoRollback {
      val allResults = Members.findAll()
      allResults.size should be_>(0)
    }
    "count all records" in new AutoRollback {
      val count = Members.countAll()
      count should be_>(0L)
    }
    "find all by where clauses" in new AutoRollback {
      val results = Members.findAllBy(sqls.eq(m.id, 123))
      results.size should be_>(0)
    }
    "count by where clauses" in new AutoRollback {
      val count = Members.countBy(sqls.eq(m.id, 123))
      count should be_>(0L)
    }
    "create new record" in new AutoRollback {
      val created = Members.create(name = "MyString", createdAt = null)
      created should not beNull
    }
    "save a record" in new AutoRollback {
      val entity = Members.findAll().head
      // TODO modify something
      val modified = entity
      val updated = Members.save(modified)
      updated should not equalTo(entity)
    }
    "destroy a record" in new AutoRollback {
      val entity = Members.findAll().head
      val deleted = Members.destroy(entity) == 1
      deleted should beTrue
      val shouldBeNone = Members.find(123)
      shouldBeNone.isDefined should beFalse
    }
    "perform batch insert" in new AutoRollback {
      val entities = Members.findAll()
      entities.foreach(e => Members.destroy(e))
      val batchInserted = Members.batchInsert(entities)
      batchInserted.size should be_>(0)
    }
  }

}
