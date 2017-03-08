package controllers


import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{LogoutEvent, Silhouette}
import models.{Link, LinkAddData}
import models.daos.LinkDAO
import models.forms.LinkForm
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import upickle.default._
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

  def addLink() = silhouette.SecuredAction.async { implicit request =>
    println("req : " + request.body)
    
    val linkData = read[LinkAddData](request.body.asText.getOrElse(""))
    // TODO verifications
    
    
    linkDAO.add(Link(id = 0,
      userId = request.identity.id,
      url = linkData.url,
      name = linkData.name,
      description = linkData.description,
      screenshot = Some(Array.emptyByteArray)) // TODO None does not work
    ) map {
      newId =>
        Logger.debug("new link with id " + newId)
//        Ok("Hi")
        val json = write[Boolean](true)
        Ok(json)
    }
    
//    LinkForm.form.bindFromRequest.fold(
//      errorForm => {
//        Logger.warn("error : " + errorForm.errors)
//        Future.successful(Ok(views.html.index(errorForm, Seq.empty, request.identity)))
//      },
//      successData => {
//        linkDAO.add(Link(id = 0,
//          userId = request.identity.id,
//          url = successData.url,
//          name = successData.name,
//          description = successData.description,
//          screenshot = Some(Array.emptyByteArray)) // TODO None does not work
//        ) map {
//          newId =>
//            Logger.debug("new link with id " + newId)
//          Redirect(routes.Application.index())
//        }
//      }
//    )
  }

  def deleteLink(linkId: Long) =
    silhouette.SecuredAction(BeingOwnerOf[DefaultEnv#A](linkId)(linkDAO)).async {
      linkDAO.delete(linkId) map { res =>
        Logger.debug("deleted " + res + " link(s)")
        Redirect(routes.Application.index())
      }
    }

  def listLinks = silhouette.SecuredAction.async { implicit req =>
    linkDAO.linksForUser(req.identity).map { links =>
      val json = write[Seq[Link]](links)
      Ok(json)
    }
  }


  def logout() = silhouette.SecuredAction.async { implicit request =>
    val result = Redirect(routes.Application.index())
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, result)
  }
}