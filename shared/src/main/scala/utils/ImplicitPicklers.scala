package utils

import models.{Folder, Link}

/**
  * Avoid the creation of a pickler every time 'read' or 'write' is called
  */
object ImplicitPicklers {
  implicit val folderPkl = upickle.default.macroRW[Folder]
  implicit val linkPkl = upickle.default.macroRW[Link]
  implicit val reqResPkl = upickle.default.macroRW[RequestResult]
}
