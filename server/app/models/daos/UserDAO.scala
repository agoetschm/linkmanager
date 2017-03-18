package models.daos

import models.User

import scala.concurrent.Future

/**
  * User dao interface
  */
trait UserDAO {
  def find(id: Long): Future[Option[User]]
  
  def find(username: String): Future[Option[User]]

  def findByEmail(email: String): Future[Option[User]]
  
  def add(user: User): Future[User]
  
  def update(user: User): Future[Option[User]]
}
