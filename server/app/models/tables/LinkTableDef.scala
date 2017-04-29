package models.tables

import models.Link
import slick.driver.PostgresDriver.api._

/**
  * Link table definition
  */
class LinkTableDef(tag: Tag) extends Table[Link](tag, "links") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def userId = column[Long]("user_id")

  def url = column[String]("url")

  def name = column[String]("name")

  def description = column[Option[String]]("description")

  def maybeParent = column[Option[Long]]("parent")

  private val users = TableQuery[UserTableDef]

  def user = foreignKey("user_fk", userId, users)(_.id,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  private val folders = TableQuery[FolderTableDef]

  def folder = foreignKey("folder_fk", maybeParent, folders)(_.id.?,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)


  override def * =
    (id, userId, url, name, description, maybeParent) <> ((Link.apply _).tupled
      // because https://github.com/VirtusLab/unicorn/issues/11
      , Link.unapply)

  //    (id, userId, url, name, description) <>[Link, (Long, Long, String, String, Option[String])]( {
  //      case ((id, userId, url, name, descr)) => Link(id, userId, url, name, descr, Root)
  //    }, {
  //      case Link(i, us, u, n, d, p) => Option((i, us, u, n, d))
  //    })

}
