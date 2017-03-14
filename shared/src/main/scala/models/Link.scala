package models

/**
  * Link model
  */
case class Link(id: Long, userId: Long, url: String, name: String,
                description: Option[String], screenshot: Option[Array[Byte]])






