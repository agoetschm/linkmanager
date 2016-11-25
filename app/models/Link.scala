package models

import java.awt.Image

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import slick.driver.MySQLDriver.api._


/**
  * Created by agoetschm on 8/3/16.
  */
case class Link(id: Long, url: String, name: String, description: Option[String], screenshot: Option[Array[Byte]])

case class LinkFromData(url: String, name: String, description: Option[String])

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
    )(LinkFromData.apply)(LinkFromData.unapply)
  )
}

class LinkTableDef(tag: Tag) extends Table[Link](tag, "link") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def url = column[String]("url")

  def name = column[String]("name")

  def description = column[Option[String]]("description")

  def screenshot = column[Option[Array[Byte]]]("screenshot")

  override def * =
    (id, url, name, description, screenshot) <> (Link.tupled, Link.unapply)

  //  <>[Link, (Long, String, String, Option[String], Option[Blob])]( {
  //    case ((id, url, name, descr, image)) => Link(id, url, name, descr, None)
  //  }, {
  //    case Link(i, u, n, d, im) => Option((i, u, n, d, None))
  //  })

  //
}

