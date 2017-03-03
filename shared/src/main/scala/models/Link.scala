package models

/**
  * Link model
  */
case class Link(id: Long, userId: Long, url: String, name: String,
                description: Option[String], screenshot: Option[Array[Byte]])

/**
  * Data to add a new link
  */
case class LinkAddData(url: String, name: String, description: Option[String])





