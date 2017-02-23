package controllers


import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import models.daos.LinkDAO
import models.{Link, LinkForm}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import utils.auth.DefaultEnv

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

  def index: Action[AnyContent] = silhouette.UserAwareAction.async { implicit req =>
    linkDAO.listAll map { links =>
      Ok(views.html.index(LinkForm.form, links, req.identity))
    }
  }

  def addLink(): Action[AnyContent] = Action.async { implicit request =>
    LinkForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.warn("error : " + errorForm.errors)
        Future.successful(Ok(views.html.index(errorForm, Seq.empty, None)))
      },
      successData => {
        linkDAO.add(Link(id = 0,
          url = successData.url,
          name = successData.name,
          description = successData.description,
          screenshot = Some(Array.emptyByteArray)) // TODO None does not work
        ) map {
          newId =>
            Logger.debug("new link with id " + newId)
            Redirect(routes.Application.index())
        }
      }
    )
  }

  def deleteLink(id: Long): Action[AnyContent] = Action.async {
    linkDAO.delete(id) map { res =>
      Logger.debug("deleted " + res + " link(s)")
      Redirect(routes.Application.index())
    }
  }

  def logout(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.Application.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}
