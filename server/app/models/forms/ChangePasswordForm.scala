package models.forms

import play.api.data.Forms._
import play.api.data._

/**
 * Change password form.
 */
object ChangePasswordForm {

  val form = Form(tuple(
    "current-password" -> nonEmptyText,
    "new-password" -> nonEmptyText
  ))
}
