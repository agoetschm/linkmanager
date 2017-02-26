package models

import java.awt.Image

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}

import slick.driver.PostgresDriver.api._


/**
  * Link model
  */
case class Link(id: Long, userId: Long, url: String, name: String, description: Option[String], screenshot: Option[Array[Byte]])

case class LinkData(url: String, name: String, description: Option[String])

object LinkForm {
  private val urlRegex = "(^|[\\s.:;?\\-\\]<\\(])(https?://[-\\w;/?:@&=+$\\|\\_.!~*\\|'()\\[\\]%#,☺]+[\\w/#](\\(\\))?)(?=$|[\\s',\\|\\(\\).:;?\\-\\[\\]>\\)])"
  private val urlContraint = Constraint[String] { s: String =>
    if (s.matches(urlRegex))
      Valid
    else
      Invalid("this field isn't a valid url")

  }

  val form = Form(
    mapping(
      "url" -> nonEmptyText.verifying(urlContraint),
      "name" -> nonEmptyText,
      "description" -> optional(text)
    )(LinkData.apply)(LinkData.unapply)
  )
}

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

