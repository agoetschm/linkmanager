package models.forms

import models.FolderAddData
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, longNumber}

/**
  * Form creation form
  */
object NewFolderForm {
  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "parentId" -> optional(longNumber)
    )(FolderAddData.apply)(FolderAddData.unapply)
  )
}
