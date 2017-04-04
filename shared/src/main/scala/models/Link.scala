package models


abstract class Entity(id: Long, userId: Long, name: String, parentId: Option[Long])

case class Link(id: Long, userId: Long, url: String, name: String,
                description: Option[String], parentId: Option[Long])
  extends Entity(id, userId, name, parentId)

case class Folder(id: Long, userId: Long, name: String, parentId: Option[Long])
  extends Entity(id, userId, name, parentId)






