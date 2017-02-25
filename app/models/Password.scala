package models

import slick.driver.PostgresDriver.api._
import slick.lifted.ForeignKeyQuery

/**
  * Model for a password (the password isn't in the user model because a user 
  * can authenticate by an other mean and therefore doesn't need one).
  */
case class Password(userId: Long, password: String)

// TODO add hasher + account activated


class PasswordTableDef(tag: Tag) extends Table[Password](tag, "passwords") {
  def userId = column[Long]("user_id", O.PrimaryKey)

  def password = column[String]("password")

  val users = TableQuery[UserTableDef]

  def user = foreignKey("user_fk", userId, users)(_.id,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  override def * = (userId, password) <> (Password.tupled, Password.unapply)
}