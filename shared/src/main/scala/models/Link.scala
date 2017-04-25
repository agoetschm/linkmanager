package models


abstract class Entity(val id: Long, val userId: Long, val name: String, val parentId: Option[Long])

case class Link(override val id: Long, override val userId: Long, url: String, override val name: String,
                description: Option[String], override val parentId: Option[Long])
  extends Entity(id, userId, name, parentId)

case class Folder(override val id: Long, override val userId: Long, override val name: String, override val parentId: Option[Long])
  extends Entity(id, userId, name, parentId)






