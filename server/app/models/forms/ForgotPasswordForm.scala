package models.forms

import play.api.data.Forms._
import play.api.data._

/**
 * Forgot password form.
 */
object ForgotPasswordForm {
  val form = Form(
    "email" -> email
  )
}
