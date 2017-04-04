package models.tables

import models.Folder
import slick.driver.PostgresDriver.api._

/**
  * Folder table definition
  */
class FolderTableDef(tag: Tag) extends Table[Folder](tag, "folders") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")

  def name = column[String]("name")

  def maybeParent = column[Option[Long]]("parent")

  private val users = TableQuery[UserTableDef]

  def user = foreignKey("user_fk", userId, users)(_.id,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  private val folders = TableQuery[FolderTableDef]

  def folder = foreignKey("folder_fk", maybeParent, folders)(_.id.?,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.SetNull)


  override def * =
    (id, userId, name, maybeParent) <> (Folder.tupled, Folder.unapply)
}
