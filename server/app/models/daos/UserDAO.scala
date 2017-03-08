package models.daos

import models.User

import scala.concurrent.Future

/**
  * User dao interface
  */
trait UserDAO {
  def find(username: String): Future[Option[User]]
  
  def add(user: User): Future[User]
}
