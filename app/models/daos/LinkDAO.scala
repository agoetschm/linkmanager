package models.daos

import models.Link

import scala.concurrent.Future

/**
  * Link dao interface
  */

trait LinkDAO {
  def add(l: Link): Future[Long]

  def delete(id: Long): Future[Int]

  def get(id: Long): Future[Option[Link]]

  def listAll: Future[Seq[Link]]
}
