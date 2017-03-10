package models.tables

import models.Password
import slick.driver.PostgresDriver.api._
import slick.lifted.ForeignKeyQuery

/**
  * Password table definition
  */
class PasswordTableDef(tag: Tag) extends Table[Password](tag, "passwords") {
  def userId = column[Long]("user_id", O.PrimaryKey)

  def password = column[String]("password")

  
  val users = TableQuery[UserTableDef]

  def user = foreignKey("user_fk", userId, users)(_.id,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  
  override def * = (userId, password) <> (Password.tupled, Password.unapply)
}
