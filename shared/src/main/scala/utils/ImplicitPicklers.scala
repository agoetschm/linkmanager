package utils

import models.Link

/**
  * Avoid the creation of a pickler every time 'read' or 'write' is called
  */
object ImplicitPicklers {
  implicit val linkPkl = upickle.default.macroRW[Link]
  implicit val reqResPkl = upickle.default.macroRW[RequestResult]
}
