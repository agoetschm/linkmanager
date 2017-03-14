package models

/**
  * Data to add a new link
  */
case class LinkAddData(url: String, name: String, description: Option[String])
