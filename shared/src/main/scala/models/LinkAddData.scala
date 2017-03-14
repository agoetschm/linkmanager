package models

/**
  * Data to add a new link
  */
case class LinkAddData(url: String, name: Option[String], description: Option[String])
