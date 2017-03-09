
import models.Link
import org.scalajs.dom.ext.Ajax
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryEventObject, JQueryXHR, jQuery}
import upickle.default._
import utils.RequestResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.Text.all._

/**
  * Main
  */
object MainApp extends JSApp {
  @JSExport
  override def main(): Unit = {

    if (jQuery("#links-list").length != 0)
      loadLinks()
    if (jQuery("#new-link-form").length != 0)
      setupLinkForm()
  }

  def setupLinkForm(): Unit = {
    val linkForm = jQuery("#new-link-form")
    linkForm.submit { (e: JQueryEventObject) =>
      e.preventDefault() // prevent reload
      postNewLink()
    }
  }

  def postNewLink(): Unit = {
    // TODO https://www.playframework.com/documentation/2.5.x/ScalaJavascriptRouting
    println("new link")

    def handleAjaxError(jqXHR: JQueryXHR, textStatus: String, errorThrow: String): Unit = {
      //      println("Error while performing AJAX POST")
      displayMessage("The creation of the new link failed.")
    }

    def handleAjaxSuccess(data: js.Any, textStatus: String, jqXHR: JQueryXHR): Unit = {
      val success = read[RequestResult](jqXHR.responseText).success
      displayMessage(
        if (success) "Successfully added a new link"
        else "The creation of the new link failed."
      )
      // clear form and reload links
      jQuery("#new-link-form input").value("")
      loadLinks()
    }

    val ajaxSettings = js.Dynamic.literal(
      url = "/addLink",
      contentType = "application/x-www-form-urlencoded",
      accept = "application/json",
      data = jQuery("#new-link-form").serialize(),
      `type` = "POST",
      success = handleAjaxSuccess _,
      error = handleAjaxError _).asInstanceOf[JQueryAjaxSettings]
    jQuery.ajax(ajaxSettings)
  }

  def loadLinks(): Unit = Ajax.get("/listLinks").onSuccess { case xhr =>
    println("load links")
    val list = jQuery("#links-list")
    list.empty() // clear list

    val links = read[Seq[Link]](xhr.responseText)
    for (link <- links) {
      // TODO twirl in client https://medium.com/@muuki88/finch-scala-js-twirl-templates-b46d2123ea78#.lc3d90joj
      val delButton = a(
        `class` := "btn-flat right waves-effect",
        //        href := "/deleteLink/" + link.id,
        onclick := "MainApp().deleteLink(" + link.id + ")",
        i(`class` := "material-icons", "delete"))
      val row = tr(
        td(a(href := link.url, link.name)),
        td(link.description),
        td(delButton)
      )
      list.append(row.render)
    }
  }

  @JSExport
  def deleteLink(linkId: Integer): Unit = {
    println("delete link " + linkId)
    Ajax.get("/deleteLink/" + linkId).onSuccess { case xhr =>
      val result = read[RequestResult](xhr.responseText)

      // toast message
      val msg = if (result.success) "Successfully deleted link" else "Deletion of link failed"
      displayMessage(msg)

      // reload whether the deletion failed or not
      loadLinks();
    }
  }

  def displayMessage(msg: String) = Materialize.toast(msg, 3000)

}
