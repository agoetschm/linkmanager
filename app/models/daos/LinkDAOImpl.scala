package models.daos

import com.google.inject.Inject
import models.{Link, LinkTableDef}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Implementation of the link dao
  */
class LinkDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends LinkDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.driver.api._

  val links = TableQuery[LinkTableDef]

  def add(l: Link): Future[Long] = 
    dbConfig.db.run(links returning links.map(_.id) += l).recover {
      case e: Exception => -1
    }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(links.filter(_.id === id).delete)
  }

  def get(id: Long): Future[Option[Link]] =
    dbConfig.db.run(links.filter(_.id === id).result.headOption)

  def listAll: Future[Seq[Link]] = dbConfig.db.run(links.result)
}