
import models.{Entity, Folder, Link}
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
  var linkDAO: DAO[Link] = _
  var folderDAO: DAO[Folder] = _

  @JSExport
  override def main(): Unit = {
    if (jQuery("#guest").length != 0) {
      linkDAO = LinkDAOGuestImpl
      folderDAO = FolderDAOGuestImpl
    }
    else {
      linkDAO = LinkDAOBackendImpl
      folderDAO = FolderDAOBackendImpl
    }
    
    if (jQuery("#links-collection").length != 0)
      loadLinks()
    if (jQuery("#new-link-form").length != 0)
      setupForms()
  }

  def setupForms(): Unit = {
    val linkForm = jQuery("#new-link-form")
    linkForm.submit { (e: JQueryEventObject) =>
      e.preventDefault() // prevent reload
      postNewLink()
    }

    val folderForm = jQuery("#new-folder-form")
    folderForm.submit { (e: JQueryEventObject) =>
      e.preventDefault() // prevent reload
      postNewFolder()
    }
  }


  // ACTIONS -------------------------------

  def loadLinks(): Unit = {
    linkDAO.getAll.recover {
      case e: ClientException =>
        println("failed to load links: " + e)
        displayMessage("Failed to load links")
        Seq()
    }.map { links =>
      folderDAO.getAll.recover {
        case e: ClientException =>
          println("failed to load folders: " + e)
          displayMessage("Failed to load links")
          Seq()
      }.map { folders =>
        // TODO not efficient, only usable on small amount of links and folders
        val linksAsEntities: Seq[Entity] = links
        val linksAndFolders = linksAsEntities ++ folders

        def buildTree(parentId: Option[Long]): Node = {
          val root: Option[Folder] = parentId.flatMap(id => folders.find(_.id == id))
          val firstLevel = linksAndFolders.filter(_.parentId == parentId)
          Node(root, firstLevel.map {
            case f: Folder => buildTree(Some(f.id))
            case l: Link => Node(Some(l), Seq())
          })
        }

        val tree: Node = buildTree(None)

        def deleteFolderId(f: Folder) = "deleteFolder" + f.id

        def deleteLinkId(l: Link) = "deleteLink" + l.id

        def treeToView(node: Node): String =
          if (node.value.isDefined)
            node.value.map {
              case f: Folder =>
                li(raw(div(`class` := "collapsible-header",
                  raw(i(`class` := "material-icons", "folder") + node.value.get.name))
                  + div(`class` := "collapsible-body", raw(
                  (if (node.children.nonEmpty)
                    ul(`class` := "collapsible", attr("data-collapsible") := "accordion",
                      raw(node.children.map(treeToView).reduce(_ + _)))
                  else
                    i("Empty folder"))
                    + div(`class` := "row no-margin-bottom",
                    div(`class` := "col s12 center",
                      a(id := deleteFolderId(f), `class` := "btn-flat waves-light", href := "#!",
                        i(`class` := "material-icons", "delete")))).render)).render)).render

              case l: Link =>
                li(raw(div(`class` := "collapsible-header",
                  raw(i(`class` := "material-icons", "link") + node.value.get.name))
                  + div(`class` := "collapsible-body",
                  raw((if (l.description.isDefined) p(l.description.get) else "") +
                    div(`class` := "row no-margin-bottom",
                      raw(div(`class` := "col s6 m3 offset-m3 center",
                        a(href := l.url, target := "_blank" /* open in new tab */ , `class` := "btn waves-light",
                          i(`class` := "material-icons", "open_in_browser")))
                        +
                        div(`class` := "col s6 m3 center",
                          a(id := deleteLinkId(l), href := "#!", `class` := "btn-flat waves-light",
                            i(`class` := "material-icons", "delete"))).render)).render)
                ).render)).render
            }.get
          else
            node.children.map(treeToView).reduce(_ + _)


        val collection = jQuery("#links-collection")
        collection.empty()
        collection.append(treeToView(tree))

        // delete listeners
        links.foreach { l =>
          val deleteListener: JQueryEventObject => Unit = { e: JQueryEventObject => deleteLink(l) }
          jQuery("#" + deleteLinkId(l)).click(deleteListener)
        }
        folders.foreach { f =>
          val deleteListener: JQueryEventObject => Unit = { e: JQueryEventObject => deleteFolder(f) }
          jQuery("#" + deleteFolderId(f)).click(deleteListener)
        }

        // activate collapsible
        Collapsible.activate()
        
        // TODO twirl in client https://medium.com/@muuki88/finch-scala-js-twirl-templates-b46d2123ea78#.lc3d90joj
      }
    }
  }

  def postNewLink(): Unit = {
    // TODO https://www.playframework.com/documentation/2.5.x/ScalaJavascriptRouting
    println("new link")

    val successMsg = "Successfully added a new link"
    val failMsg = "The creation of the new link failed."

    linkDAO.post("new-link-form").recover {
      case e: ClientException =>
        println("error on post new link: " + e.getMessage)
        displayMessage(failMsg)
        RequestResult(success = false, None)
    }.map { result =>
      displayMessage(
        if (result.success) successMsg
        else failMsg + " " + result.error.getOrElse("")
      )
      // reset form and reload links
      jQuery("#new-link-form").trigger("reset")
      loadLinks()
    }
  }

  def postNewFolder(): Unit = {
    println("new folder")

    val successMsg = "Successfully added a new folder"
    val failMsg = "The creation of the new folder failed."

    folderDAO.post("new-folder-form").recover {
      case e: ClientException =>
        println("error on post new folder: " + e.getMessage)
        displayMessage(failMsg)
        RequestResult(success = false, None)
    }.map { result =>
      displayMessage(
        if (result.success) successMsg
        else failMsg + " " + result.error.getOrElse("")
      )
      // reset form and reload links
      jQuery("#new-folder-form").trigger("reset")
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
    Modal.confirmDelete("modal-delete-link")
  }

  def deleteFolder(folder: Folder): Unit = {
    println("delete folder " + folder.id)
    val confirmBut = jQuery("#button-confirm-delete-folder")
    confirmBut.off("click") // remove old handler
    confirmBut.click((e: JQueryEventObject) => {
      performDeleteFolder(folder)
    })
    Modal.confirmDelete("modal-delete-folder")
  }

  def performDeleteLink(link: Link): Unit = {
    val successMsg = "Successfully deleted link"
    val failMsg = "Deletion of link failed"

    linkDAO.delete(link).recover {
      case e: ClientException =>
        RequestResult(success = false, None)
    }.map { result =>
      // toast message
      val msg = if (result.success) successMsg else failMsg
      displayMessage(msg)
      // reload whether the deletion failed or not
      loadLinks()
    }
  }

  def performDeleteFolder(folder: Folder): Unit = {
    val successMsg = "Successfully deleted folder and its content"
    val failMsg = "Deletion of folder failed"

    folderDAO.delete(folder).recover {
      case e: ClientException =>
        RequestResult(success = false, None)
    }.map { result =>
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

  // DAOS ---------------------------

  trait DAO[T] {
    def getAll: Future[Seq[T]]

    def delete(t: T): Future[RequestResult]

    def post(formId: String): Future[RequestResult]
  }

  object LinkDAOBackendImpl extends DAO[Link] {
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

    override def post(formId: String) = {
      val p = Promise[RequestResult]()
      ajax("/addLink", "POST", Some(jQuery("#" + formId).serialize()),
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

  object FolderDAOBackendImpl extends DAO[Folder] {
    override def getAll = {
      val p = Promise[Seq[Folder]]()
      ajax("/listFolders", "GET", maybeData = None,
        successHandler = { jqXHR =>
          try p.success(read[Seq[Folder]](jqXHR.responseText))
          catch {
            case e: Exception =>
              p.failure(UPickleException("failed to parse folders"))
          }
        },
        errorHandler = () => p.failure(AjaxException("failed to retrieve folders from server"))
      )
      p.future
    }

    override def delete(folder: Folder) = {
      val p = Promise[RequestResult]()
      ajax("/deleteFolder/" + folder.id, "GET", maybeData = None,
        successHandler = jqXHR => {
          try p.success(read[RequestResult](jqXHR.responseText))
          catch {
            case e: Exception =>
              p.failure(UPickleException("failed to parse result of folder deltion"))
          }
        },
        errorHandler = () => p.failure(AjaxException("failed to delete folder"))
      )
      p.future
    }

    override def post(formId: String) = {
      val p = Promise[RequestResult]()
      ajax("/addFolder", "POST", Some(jQuery("#" + formId).serialize()),
        successHandler = jqXHR => {
          try p.success(read[RequestResult](jqXHR.responseText))
          catch {
            case e: Exception =>
              p.failure(UPickleException("failed to parse result of folder creation"))
          }
        },
        errorHandler = () => p.failure(AjaxException("failed to create a new foldercd SDocd"))
      )
      p.future
    }
  }

  /**
    * Fake DAO for the guest user
    */
  object LinkDAOGuestImpl extends DAO[Link] {
    var lastLinkId = 1L
    val guestLinks: mutable.HashMap[Long, Link] = mutable.HashMap()
    guestLinks.put(1L, Link(1L, 0L, "http://example.com", "Example", None, None))
    guestLinks.put(2L, Link(2L, 0L, "http://google.com", "google.com", Some("The Google search engine"), Some(1L)))

    override def getAll = Future.successful(guestLinks.values.toSeq)

    override def delete(link: Link) = {
      guestLinks.remove(link.id)
      Future.successful(RequestResult(success = true))
    }

    override def post(formId: String) = {
      val url = jQuery("#" + formId + " #url").value().toString
      val name = jQuery("#" + formId + " #name").value().toString
      val description = jQuery("#" + formId + " #description").value().toString

      lastLinkId += 1
      val newLink = Link(lastLinkId, 0L, url,
        if (name.length > 0) name else url.replaceFirst("https?://", ""),
        if (description.length > 0) Some(description) else None,
        None)

      guestLinks.put(lastLinkId, newLink)
      Future.successful(RequestResult(success = true))
    }
  }

  object FolderDAOGuestImpl extends DAO[Folder] {
    val guestFolders: mutable.HashMap[Long, Folder] = mutable.HashMap()
    guestFolders.put(1L, Folder(1L, 0L, "Folder 1", None))
    guestFolders.put(2L, Folder(2L, 0L, "Folder 2", None))
    guestFolders.put(3L, Folder(3L, 0L, "Folder 3", Some(1L)))
    var lastFolderId = 3L

    override def getAll = Future.successful(guestFolders.values.toSeq)

    override def delete(folder: Folder) = {
      guestFolders.remove(folder.id)
      // TODO cascade deletion
      Future.successful(RequestResult(success = true))
    }

    override def post(formId: String) = {
      val name = jQuery("#" + formId + " #name").value().toString

      lastFolderId += 1
      val newFolder = Folder(lastFolderId, 0L, name, None)

      guestFolders.put(lastFolderId, newFolder)
      Future.successful(RequestResult(success = true))
    }
  }

  // TREE STRUCTURE --------
  case class Node(value: Option[Entity], children: Seq[Node])

}


