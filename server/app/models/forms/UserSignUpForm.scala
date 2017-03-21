package models.forms

import models.UserSignUpData
import play.api.data.Form
import play.api.data.Forms._

/**
  * Sign up form
  */
object UserSignUpForm {
  val form = Form(
    mapping(
      "username" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(UserSignUpData.apply)(UserSignUpData.unapply)
  )
}
