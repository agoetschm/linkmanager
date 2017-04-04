
import models.Link
import org.scalajs.jquery.{JQueryAjaxSettings, JQueryEventObject, JQueryXHR, jQuery}
import upickle.default._
import utils.ImplicitPicklers._
import utils.RequestResult

import scala.collection.mutable
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
    if (jQuery("#guest").length != 0) {
      if (jQuery("#links-list").length != 0)
        loadLinksGuest()
      if (jQuery("#new-link-form").length != 0)
        setupLinkFormGuest()
    } else {
      if (jQuery("#links-list").length != 0)
        loadLinks()
      if (jQuery("#new-link-form").length != 0)
        setupLinkForm()
    }
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

        val links = {
          try read[Seq[Link]](jqXHR.responseText)
          catch {
            case e: Exception =>
              displayMessage("Failed to load links")
              Seq()
          }
        }


        for (link <- links) {
          // TODO twirl in client https://medium.com/@muuki88/finch-scala-js-twirl-templates-b46d2123ea78#.lc3d90joj
          val delButId = "deleteLink" + link.id
          val delButton = a(
            id := delButId,
            `class` := "btn-flat right waves-effect",
            //        href := "/deleteLink/" + link.id,
//            onclick := "MainApp().deleteLink(" + link.id + ")",
            i(`class` := "material-icons", "delete"))
          val row = tr(
            td(a(href := link.url, target := "_blank" /* open in new tab */ , link.name)),
            td(link.description),
            td(delButton)
          )
          list.append(row.render)
          val deleteListener: JQueryEventObject => Unit = { e: JQueryEventObject => deleteLink(link) }
          jQuery("#" + delButId).click(deleteListener)
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
        val result =
          try read[RequestResult](jqXHR.responseText)
          catch {
            case e: Exception =>
              RequestResult(success = false, Some(failMsg))
          }
        println("result = " + result)
        displayMessage(
          if (result.success) successMsg
          else failMsg + " " + result.error.getOrElse("")
        )
        // reset form and reload links
        jQuery("#new-link-form").trigger("reset")
        loadLinks()
      },
      errorHandler = () => displayMessage(failMsg)
    )
  }


  def deleteLink(link: Link): Unit = {
    println("delete link " + link.id)

    // modify the modal callback to delete the right link
    val confirmBut = jQuery("#button-confirm-delete-link")
    confirmBut.off("click") // remove old handler
    confirmBut.click((e: JQueryEventObject) => {
      println("confirm delete link " + link.id)
      performDeleteLink(link)
    })
    // open the modal
    Modal.confirmDeleteLink("modal-delete-link")
  }

  def performDeleteLink(link: Link): Unit = {
    val successMsg = "Successfully deleted link"
    val failMsg = "Deletion of link failed"

    ajax("/deleteLink/" + link.id, "GET", maybeData = None,
      successHandler = jqXHR => {
        val success = {
          try read[RequestResult](jqXHR.responseText)
          catch {
            case e: Exception =>
              RequestResult(success = false, Some(failMsg))
          }
        }.success
        // toast message
        val msg = if (success) successMsg else failMsg
        displayMessage(msg)
        // reload whether the deletion failed or not
        loadLinks()
      },
      errorHandler = () => displayMessage(failMsg)
    )
  }

  // FAKE FUNCTIONS FOR GUEST USER
  var lastLinkId = 1L
  val guestLinks: mutable.HashMap[Long, Link] = mutable.HashMap()
  guestLinks.put(1L, Link(1L, 0L, "http://example.com", "example.com", None, None))

  def setupLinkFormGuest(): Unit = {
    val linkForm = jQuery("#new-link-form")
    linkForm.submit { (e: JQueryEventObject) =>
      e.preventDefault() // prevent reload
      postNewLinkGuest()
    }
  }

  def loadLinksGuest(): Unit = {
    val list = jQuery("#links-list")
    list.empty() // clear list

    for (link <- guestLinks.values) {
      val delButId = "deleteLink" + link.id
      val delButton = a(
        id := delButId,
        `class` := "btn-flat right waves-effect",
        //        href := "/deleteLink/" + link.id,
        //        onclick := "MainApp().deleteLinkGuest(" + link.id + ")", // doesn't work with CSP!
        i(`class` := "material-icons", "delete"))
      val row = tr(
        td(a(href := link.url, target := "_blank" /* open in new tab */ , link.name)),
        td(link.description),
        td(delButton)
      )
      list.append(row.render)
      val deleteListener: JQueryEventObject => Unit = { e: JQueryEventObject => deleteLinkGuest(link) }
      jQuery("#" + delButId).click(deleteListener)
    }
  }

  def postNewLinkGuest(): Unit = {
    val url = jQuery("#url").value().toString
    val name = jQuery("#name").value().toString
    val description = jQuery("#description").value().toString

    lastLinkId += 1
    val newLink = Link(lastLinkId, 0L, url,
      if (name.length > 0) name else url.replaceFirst("https?://", ""),
      if (description.length > 0) Some(description) else None,
      None)

    guestLinks.put(lastLinkId, newLink)

    // reset form and reload links
    jQuery("#new-link-form").trigger("reset")
    loadLinksGuest()
  }

  def deleteLinkGuest(link: Link): Unit = {
    println("delete link " + link.id)

    guestLinks.remove(link.id)

    loadLinksGuest()
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
