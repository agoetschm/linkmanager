package models.forms

import models.FolderAddData
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}

/**
  * Form creation form
  */
object NewFolderForm {
  val form = Form(
    mapping(
      "name" -> nonEmptyText
    )(FolderAddData.apply)(FolderAddData.unapply)
  )
}
