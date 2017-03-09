package controllers


import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import models.{Link, LinkAddData}
import models.daos.LinkDAO
import models.forms.LinkForm
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import upickle.default._
import utils.RequestResult
import utils.auth.{BeingOwnerOf, DefaultEnv}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Home controller
  */
class Application @Inject()(
                             silhouette: Silhouette[DefaultEnv],
                             linkDAO: LinkDAO,
                             val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def index = silhouette.SecuredAction.async { implicit req =>
    linkDAO.linksForUser(req.identity).map { links =>
      Ok(views.html.index(LinkForm.form, links, req.identity))
    }
  }

  def addLink() = silhouette.SecuredAction.async { implicit req =>
    LinkForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.warn("error : " + errorForm.errors)
        Future.successful(Ok(write(RequestResult(success = false))))
        //        Future.successful(Ok(views.html.index(errorForm, Seq.empty, req.identity)))
      },
      successData => {
        linkDAO.add(Link(id = 0,
          userId = req.identity.id,
          url = successData.url,
          name = successData.name,
          description = successData.description,
          screenshot = Some(Array.emptyByteArray)) // TODO None does not work
        ) map {
          maybeNewId =>
            Logger.debug("new link with id " + maybeNewId)
            val result = RequestResult(maybeNewId.isDefined)
            Ok(write(result))
        }
      }
    )
  }

  def deleteLink(linkId: Long) =
    silhouette.SecuredAction(BeingOwnerOf[DefaultEnv#A](linkId)(linkDAO)).async {
      linkDAO.delete(linkId) map { success =>
        Logger.debug("delete success = " + success)
        Ok(write(RequestResult(success)))
      }
    }

  def listLinks = silhouette.SecuredAction.async { implicit req =>
    linkDAO.linksForUser(req.identity).map { links =>
      val pickeled = write[Seq[Link]](links)
      Ok(pickeled)
    }
  }


  def logout() = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.Application.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
