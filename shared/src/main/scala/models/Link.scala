package models


abstract class Entity(val id: Long, userId: Long, val name: String, val parentId: Option[Long])

case class Link(override val id: Long, userId: Long, url: String, override val name: String,
                description: Option[String], override val parentId: Option[Long])
  extends Entity(id, userId, name, parentId)

case class Folder(override val id: Long, userId: Long, override val name: String, override val parentId: Option[Long])
  extends Entity(id, userId, name, parentId)






