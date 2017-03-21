package models.forms

import play.api.data.Forms._
import play.api.data._

/**
 * Reset password form.
 */
object ResetPasswordForm {

  val form = Form(
    "password" -> nonEmptyText
  )
}
