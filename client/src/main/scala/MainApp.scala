
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
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}


/**
  * Main
  */
object MainApp extends JSApp {
  var linkDAO: LinkDAO = _

  @JSExport
  override def main(): Unit = {
    if (jQuery("#guest").length != 0)
      linkDAO = LinkDAOGuestImpl
    else
      linkDAO = LinkDAOBackendImpl

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

  def loadLinks(): Unit = {
    val list = jQuery("#links-list")
    list.empty() // clear list

    linkDAO.getAll.recover {
      case e: ClientException =>
        println("failed to load links: " + e)
        displayMessage("Failed to load links")
        Seq()
    }.map { links =>
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
    }
  }

  def postNewLink(): Unit = {
    // TODO https://www.playframework.com/documentation/2.5.x/ScalaJavascriptRouting
    println("new link")

    val successMsg = "Successfully added a new link"
    val failMsg = "The creation of the new link failed."

    linkDAO.post().recover{
      case e:ClientException =>
        println("error on post new link: " + e.getMessage)
        displayMessage(failMsg)
        RequestResult(success = false, None)
    }.map{ result =>
      displayMessage(
        if (result.success) successMsg
        else failMsg + " " + result.error.getOrElse("")
      )
      // reset form and reload links
      jQuery("#new-link-form").trigger("reset")
      loadLinks()
    }
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
    
    linkDAO.delete(link).recover{
      case e:ClientException =>
        RequestResult(success = false, None)
    }.map{ result =>
      // toast message
      val msg = if (result.success) successMsg else failMsg
      displayMessage(msg)
      // reload whether the deletion failed or not
      loadLinks()
    }
  }


  // UTILS ------------------------------------

  def showProgress() = jQuery("#progress").addClass("scale-in")

  def hideProgress() = jQuery("#progress").removeClass("scale-in")

  def displayMessage(msg: String) = Materialize.toast(msg, 4000)
  
  def ajax(url: String, reqType: String, maybeData: Option[String],
           successHandler: JQueryXHR => Unit, errorHandler: () => Unit): Unit = {
    def handleAjaxError(jqXHR: JQueryXHR, textStatus: String, errorThrow: String): Unit = {
      hideProgress()
      errorHandler()
    }

    def handleAjaxSuccess(data: js.Any, textStatus: String, jqXHR: JQueryXHR): Unit = {
      hideProgress()
      successHandler(jqXHR)
    }

    val data: String = maybeData.getOrElse("")

    val ajaxSettings = js.Dynamic.literal(
      url = url,
      //      contentType = "application/x-www-form-urlencoded", is default
      accept = "application/json",
      data = data,
      `type` = reqType,
      success = handleAjaxSuccess _,
      error = handleAjaxError _).asInstanceOf[JQueryAjaxSettings]
    
    showProgress()
    jQuery.ajax(ajaxSettings)
  }
  
  // LINK DAO ---------------------------
  
  trait LinkDAO {
    def getAll: Future[Seq[Link]]

    def delete(link: Link): Future[RequestResult]

    def post(/*link: Link*/): Future[RequestResult]
  }

  object LinkDAOBackendImpl extends LinkDAO {
    override def getAll = {
      val p = Promise[Seq[Link]]()
      ajax("/listLinks", "GET", maybeData = None,
        successHandler = { jqXHR =>
          try p.success(read[Seq[Link]](jqXHR.responseText))
          catch {
            case e: Exception =>
              p.failure(UPickleException("failed to parse links"))
          }
        },
        errorHandler = () => p.failure(AjaxException("failed to retrieve links from server"))
      )
      p.future
    }

    override def delete(link: Link) = {
      val p = Promise[RequestResult]()
      ajax("/deleteLink/" + link.id, "GET", maybeData = None,
        successHandler = jqXHR => {
          try p.success(read[RequestResult](jqXHR.responseText))
          catch {
            case e: Exception =>
              p.failure(UPickleException("failed to parse result of link deltion"))
          }
        },
        errorHandler = () => p.failure(AjaxException("failed to delete link"))
      )
      p.future
    }

    override def post() = {
      val p = Promise[RequestResult]()
      ajax("/addLink", "POST", Some(jQuery("#new-link-form").serialize()),
        successHandler = jqXHR => {
          try p.success(read[RequestResult](jqXHR.responseText))
          catch {
            case e: Exception =>
              p.failure(UPickleException("failed to parse result of link creation"))
          }
        },
        errorHandler = () => p.failure(AjaxException("failed to create a new link"))
      )
      p.future
    }
  }

  /**
    * Fake DAO for the guest user
    */
  object LinkDAOGuestImpl extends LinkDAO {
    var lastLinkId = 1L
    val guestLinks: mutable.HashMap[Long, Link] = mutable.HashMap()
    guestLinks.put(1L, Link(1L, 0L, "http://example.com", "example.com", None, None))

    override def getAll = Future.successful(guestLinks.values.toSeq)

    override def delete(link: Link) = {
      guestLinks.remove(link.id)
      Future.successful(RequestResult(success = true))
    }

    override def post() = {
      val url = jQuery("#url").value().toString
      val name = jQuery("#name").value().toString
      val description = jQuery("#description").value().toString

      lastLinkId += 1
      val newLink = Link(lastLinkId, 0L, url,
        if (name.length > 0) name else url.replaceFirst("https?://", ""),
        if (description.length > 0) Some(description) else None,
        None)

      guestLinks.put(lastLinkId, newLink)
      Future.successful(RequestResult(success = true))
    }
  }

}


