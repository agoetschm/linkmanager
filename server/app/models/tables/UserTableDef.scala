package models.tables


import models.User
import slick.driver.PostgresDriver.api._

/**
  * User table definition
  */
class UserTableDef(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def username = column[String]("username")

  def email = column[String]("email")

  def activated = column[Boolean]("activated")

  override def * = (id, username, email, activated) <> (User.tupled, User.unapply)
}
