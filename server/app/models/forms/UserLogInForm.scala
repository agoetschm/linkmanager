package models.forms

import models.UserLogInData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Log in form
  */
object UserLogInForm {
  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLogInData.apply)(UserLogInData.unapply)
  )
}
