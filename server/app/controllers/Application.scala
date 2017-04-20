package controllers


import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import models.{Folder, Link}
import models.daos.{FolderDAO, LinkDAO}
import models.forms.NewLinkForm
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import upickle.default._
import utils.RequestResult
import utils.auth.{BeingOwnerOf, DefaultEnv}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.ImplicitPicklers._

/**
  * Home controller
  */
class Application @Inject()(
                             silhouette: Silhouette[DefaultEnv],
                             linkDAO: LinkDAO,
                             folderDAO: FolderDAO,
                             val messagesApi: MessagesApi)
  extends Controller with I18nSupport {


  def index = silhouette.SecuredAction { implicit req =>
    Ok(views.html.index(NewLinkForm.form, Some(req.identity)))
  }

  def addLink() = silhouette.SecuredAction.async { implicit req =>
    NewLinkForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.warn("error : " + errorForm.errors)
        // only first error msg
        val errorStr: String = errorForm.errors.map(e => "The field '" + e.key + "' " + e.message).mkString(" ")
        Future.successful(Ok(write(RequestResult(success = false, Some(errorStr)))))
        //        Future.successful(Ok(views.html.index(errorForm, Seq.empty, req.identity)))
      },
      successData => {
        val name = successData.name.getOrElse(
          successData.url.replaceFirst("https?://", "")) // set name to url by default

        linkDAO.add(Link(id = 0,
          userId = req.identity.id,
          url = successData.url,
          name = name,
          description = successData.description,
          parentId = None)
        ) map {
          maybeNewId =>
            Logger.info("new link with id " + maybeNewId)
            val result = RequestResult(maybeNewId.isDefined)
            Ok(write(result))
        }
      }
    )
  }

  def deleteLink(linkId: Long) =
    silhouette.SecuredAction(BeingOwnerOf[DefaultEnv#A](linkId)(linkDAO)).async {
      linkDAO.delete(linkId) map { success =>
        Logger.info("delete success = " + success)
        Ok(write(RequestResult(success)))
      }
    }

  def listLinks = silhouette.SecuredAction.async { implicit req =>
    linkDAO.linksForUser(req.identity).map { links =>
      val pickled = write[Seq[Link]](links)
      //      val pickeled = write[Seq[Int]](Seq(1, 2, 3))
      assert(implicitly[Reader[Link]] eq implicitly[Reader[Link]])
      Ok(pickled)
    }
  }
  def listFolders = silhouette.SecuredAction.async { implicit req =>
    folderDAO.foldersForUser(req.identity).map { folders =>
      val pickled = write[Seq[Folder]](folders)
      Ok(pickled)
    }
  }


  def logout() = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.Application.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }

  def guest() = silhouette.UnsecuredAction { implicit req =>
    Ok(views.html.index(NewLinkForm.form, None))
  }
}
