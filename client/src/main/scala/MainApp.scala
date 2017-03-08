
import models.{Link, LinkAddData}
import org.scalajs.dom.ext.Ajax
import org.scalajs.jquery._
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
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
    linkForm.submit((e: JQueryEventObject) => postNewLink())
  }

  def postNewLink(): Unit = {
    println("new link")

    val linkForm = jQuery("#new-link-form")
    val linkData = LinkAddData(
      linkForm.find("input[name='url']").value.toString,
      linkForm.find("input[name='name']").value.toString, {
        val descr = linkForm.find("input[name='description']").value.toString
        if (descr.isEmpty) None else Some(descr)
      }
    )

    // TODO https://www.playframework.com/documentation/2.5.x/ScalaJavascriptRouting
    Ajax.post("/addLink", data = write(linkData)).onSuccess { case xhr =>
      loadLinks();
    }
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
      loadLinks();
    }
  }
}
