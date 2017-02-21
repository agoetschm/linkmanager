package models

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

import slick.driver.PostgresDriver.api._


/**
  * User model
  */
case class User(id: Long, username: String, password: String) extends Identity

case class UserLogInData(username: String, password: String)

object UserLogInForm {
  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLogInData.apply)(UserLogInData.unapply)
  )
}

//class LinkTableDef(tag: Tag) extends Table[Link](tag, "link") {
//  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
//
//  def url = column[String]("url")
//
//  def name = column[String]("name")
//
//  def description = column[Option[String]]("description")
//
//  def screenshot = column[Option[Array[Byte]]]("screenshot")
//
//  override def * =
//    (id, url, name, description, screenshot) <> (Link.tupled, Link.unapply)
//}

