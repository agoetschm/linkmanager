package models.daos

import models.{Folder, User}

import scala.concurrent.Future

/**
  * Folder dao interface
  */

trait FolderDAO {
  def add(f: Folder): Future[Option[Long]]

  def delete(id: Long): Future[Boolean]

  def get(id: Long): Future[Option[Folder]]

  def foldersForUser(user: User): Future[Seq[Folder]]
}
