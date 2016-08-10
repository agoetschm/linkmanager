package dao

import com.google.inject.Inject
import models.{Link, LinkTableDef}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by agoetschm on 8/4/16.
  */

class LinkDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val links = TableQuery[LinkTableDef]

  def add(l: Link): Future[Long] = {
    dbConfig.db.run(links returning links.map(_.id) += l).recover{
      case e: Exception => -1
    }
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(links.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Link]] =
    dbConfig.db.run(links.filter(_.id === id).result.headOption)

  def listAll: Future[Seq[Link]] = dbConfig.db.run(links.result)
}
