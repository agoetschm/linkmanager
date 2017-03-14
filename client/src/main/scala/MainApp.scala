
import models.Link
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryEventObject, JQueryXHR, jQuery}
import upickle.Invalid
import upickle.default._
import utils.RequestResult

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.Text.all._
import utils.ImplicitPicklers._


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


  // ACTIONS -------------------------------

  def loadLinks(): Unit =
    ajax("/listLinks", "GET", maybeData = None,
      successHandler = { jqXHR =>
        println("load links")
        val list = jQuery("#links-list")
        list.empty() // clear list

        val links = read[Seq[Link]](jqXHR.responseText)

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
      },
      errorHandler = () => displayMessage("Failed to load links")
    )

  def postNewLink(): Unit = {
    // TODO https://www.playframework.com/documentation/2.5.x/ScalaJavascriptRouting
    println("new link")

    val successMsg = "Successfully added a new link"
    val failMsg = "The creation of the new link failed."

    ajax("/addLink", "POST", Some(jQuery("#new-link-form").serialize()),
      successHandler = jqXHR => {
        val success = read[RequestResult](jqXHR.responseText).success
        println("success = " + success)
        displayMessage(
          if (success) successMsg
          else failMsg
        )
        // reset form and reload links
        jQuery("#new-link-form").trigger("reset")
        loadLinks()
      },
      errorHandler = () => displayMessage(failMsg)
    )
  }


  @JSExport
  def deleteLink(linkId: Integer /* should be Long, but then needs 'L' */): Unit = {
    println("delete link " + linkId)

    // modify the modal callback to delete the right link
    val confirmBut = jQuery("#button-confirm-delete-link")
    confirmBut.off("click") // remove old handler
    confirmBut.click((e: JQueryEventObject) => {
      println("confirm delete link " + linkId)
      performDeleteLink(linkId)
    })
    // open the modal
    Modal.confirmDeleteLink("modal-delete-link")
  }

  def performDeleteLink(linkId: Integer): Unit = {
    val successMsg = "Successfully deleted link"
    val failMsg = "Deletion of link failed"

    ajax("/deleteLink/" + linkId, "GET", maybeData = None,
      successHandler = jqXHR => {
        val success = read[RequestResult](jqXHR.responseText).success
        // toast message
        val msg = if (success) successMsg else failMsg
        displayMessage(msg)
        // reload whether the deletion failed or not
        loadLinks()
      },
      errorHandler = () => displayMessage(failMsg)
    )
  }


  // UTILS ------------------------------------

  def displayMessage(msg: String) = Materialize.toast(msg, 4000)


  def ajax(url: String, reqType: String, maybeData: Option[String],
           successHandler: JQueryXHR => Unit, errorHandler: () => Unit): Unit = {
    def handleAjaxError(jqXHR: JQueryXHR, textStatus: String, errorThrow: String): Unit =
      errorHandler()

    def handleAjaxSuccess(data: js.Any, textStatus: String, jqXHR: JQueryXHR): Unit =
      successHandler(jqXHR)

    val data: String = maybeData.getOrElse("")

    val ajaxSettings = js.Dynamic.literal(
      url = url,
      //      contentType = "application/x-www-form-urlencoded", is default
      accept = "application/json",
      data = data,
      `type` = reqType,
      success = handleAjaxSuccess _,
      error = handleAjaxError _).asInstanceOf[JQueryAjaxSettings]
    jQuery.ajax(ajaxSettings)
  }
}
