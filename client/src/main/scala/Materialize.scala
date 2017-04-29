import scala.scalajs.js

/**
  * Javascript interface of Materialize (only what we need)
  */
@js.native
object Materialize extends js.Object {
  def toast(msg: String, durationMs: Int): Unit = js.native

  def updateTextFields(): Unit = js.native
}

@js.native
object Modal extends js.Object {
  def confirmDelete(modalId: String): Unit = js.native
}

@js.native
object Collapsible extends js.Object {
  def activate(): Unit = js.native
}
