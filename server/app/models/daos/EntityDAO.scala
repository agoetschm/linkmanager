package models.daos

import models.{Entity, User}

import scala.concurrent.Future

/**
  * DAO interface for both links and folders
  */
trait EntityDAO[E <: Entity] {

  def add(f: E): Future[Option[Long]]

  def delete(id: Long): Future[Boolean]

  def get(id: Long): Future[Option[E]]

  def allForUser(user: User): Future[Seq[E]]
}
