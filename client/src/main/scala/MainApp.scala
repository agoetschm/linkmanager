
import models.Link
import org.scalajs.jquery.{JQuery, JQueryAjaxSettings, JQueryXHR, jQuery}

import scala.scalajs.js
import scala.scalajs.js.JSApp
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax

import scalatags.Text.all._
import upickle.default._

import scala.scalajs.js.annotation.JSExport
import dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Main
  */
object MainApp extends JSApp {
  @JSExport
  override def main(): Unit = {

    val linksListDest = jQuery("#links-list")
    if (linksListDest.length != 0)
      loadLinks()
  }

  def loadLinks(): Unit = Ajax.get("/listLinks").onSuccess { case xhr =>
    val list = jQuery("#links-list")
    
    val links = read[Seq[Link]](xhr.responseText)
    for (link <- links) {
      // TODO twirl in client https://medium.com/@muuki88/finch-scala-js-twirl-templates-b46d2123ea78#.lc3d90joj
      val delButton = a(
        `class` := "btn-flat right waves-effect",
//        href := "/deleteLink/" + link.id,
        onclick:="MainApp().deleteLink(" + link.id + ")", 
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
      jQuery("#links-list").empty() // clear list
      loadLinks();
    }
  }
}
