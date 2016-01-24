package controllers

import play.api._
import play.api.mvc._
import scalikejdbc._

object Message extends Controller {
  implicit val session = AutoSession

  def index = Action {
    val accounts = {
      try sql"select * from accounts".toMap.list.apply()
      catch { case e: Exception =>
        sql"create table accounts(id int primary key not null, name varchar(100) not null)".execute.apply()
        Seq(1 -> "Alice", 2 -> "Bob", 3 -> "Chris").foreach { case (id, name) =>
          sql"insert into accounts values ($id, $name)".update.apply()
        }
        sql"select * from accounts".toMap.list.apply()
      }
    }
    Ok(accounts.toString)
  }

  def show(id: String) = Action {
    val accounts = {
      try sql"select * from accounts where id = $id".toMap.list.apply()
      catch { case e: Exception =>
        sql"create table accounts(id int primary key not null, name varchar(100) not null)".execute.apply()
        Seq(1 -> "Alice", 2 -> "Bob", 3 -> "Chris").foreach { case (id, name) =>
          sql"insert into accounts values ($id, $name)".update.apply()
        }
        sql"select * from accounts where id = $id".toMap.single.apply()
      }
    }
    Ok(accounts.toString)
  }
}
