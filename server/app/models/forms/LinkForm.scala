package models.forms

import models.LinkData
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import play.api.data.validation.{Constraint, Invalid, Valid}

/**
  * Link creation form
  */
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
