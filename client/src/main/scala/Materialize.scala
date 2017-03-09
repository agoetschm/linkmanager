import scala.scalajs.js

/**
  * Javascript interface of Materialize (only what we use)
  */
@js.native
object Materialize extends js.Object {
  def toast(msg: String, durationMs: Int): Unit = js.native
}
