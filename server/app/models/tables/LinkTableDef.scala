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

  def screenshot = column[Option[Array[Byte]]]("screenshot")


  private val users = TableQuery[UserTableDef]

  def user = foreignKey("user_fk", userId, users)(_.id,
    onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  
  override def * =
    (id, userId, url, name, description, screenshot) <> (Link.tupled, Link.unapply)

  //  <>[Link, (Long, String, String, Option[String], Option[Blob])]( {
  //    case ((id, url, name, descr, image)) => Link(id, url, name, descr, None)
  //  }, {
  //    case Link(i, u, n, d, im) => Option((i, u, n, d, None))
  //  })
}
