package models.daos

import com.google.inject.Inject
import models.tables.FolderTableDef
import models.{Folder, User}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Implementation of the folder dao
  */
class FolderDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends EntityDAO[Folder] with HasDatabaseConfigProvider[JdbcProfile] {


  import dbConfig.driver.api._

  val folders = TableQuery[FolderTableDef]

  def add(newFolder: Folder): Future[Option[Long]] = {
    db.run(folders returning folders.map(_.id) += newFolder).recover {
      case e: Exception =>
        Logger.debug("failed to add folder: " + e.getMessage)
        -1L
    }.map { id =>
      if (id >= 0) Some(id)
      else None
    }
  }

  def delete(idToDelete: Long): Future[Boolean] = {
    db.run(folders.filter(_.id === idToDelete).delete)
      .map(amountDeleted => amountDeleted > 0)
  }

  def get(id: Long): Future[Option[Folder]] =
    db.run(folders.filter(_.id === id).result.headOption)

  def allForUser(user: User): Future[Seq[Folder]] =
    db.run(folders.filter(_.userId === user.id).result)
}