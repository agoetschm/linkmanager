package models

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

import slick.driver.PostgresDriver.api._


/**
  * User model
  */
case class User(id: Long, username: String) extends Identity

case class UserLogInData(username: String, password: String)

case class UserSignUpData(username: String, password: String)



class UserTableDef(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def username = column[String]("username")

  override def * = (id, username) <> (User.tupled, User.unapply)
}
