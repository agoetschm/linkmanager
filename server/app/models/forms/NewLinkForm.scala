package models.forms

import models.LinkAddData
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, text, longNumber}
import play.api.data.validation.{Constraint, Invalid, Valid}

/**
  * Link creation form
  */
object NewLinkForm {
  private val urlRegex = "(^|[\\s.:;?\\-\\]<\\(])(https?://[-\\w;/?:@&=+$\\|\\_.!~*\\|'()\\[\\]%#,☺]+[\\w/#](\\(\\))?)(?=$|[\\s',\\|\\(\\).:;?\\-\\[\\]>\\)])"
  private val urlContraint = Constraint[String] { s: String =>
    if (s.matches(urlRegex))
      Valid
    else
      Invalid("is not a valid url")

  }

  val form = Form(
    mapping(
      "url" -> nonEmptyText.verifying(urlContraint),
      "name" -> optional(text),
      "description" -> optional(text),
      "parentId" -> optional(longNumber)
    )(LinkAddData.apply)(LinkAddData.unapply)
  )
}
