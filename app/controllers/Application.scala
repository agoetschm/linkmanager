package controllers


import com.google.inject.Inject
import models.daos.LinkDAO
import models.{Link, LinkForm}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Home controller
  */
class Application @Inject()
(val linkDAO: LinkDAO, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def index = Action.async { implicit req =>
    linkDAO.listAll map { links =>
      Ok(views.html.index(LinkForm.form, links))
    }
  }

  def addLink() = Action.async { implicit request =>
    LinkForm.form.bindFromRequest.fold(
      errorForm => {
        Logger.warn("error : " + errorForm.errors)
        Future.successful(Ok(views.html.index(errorForm, Seq.empty)))
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

  def deleteLink(id: Long) = Action.async {
    linkDAO.delete(id) map { res =>
      Logger.debug("deleted " + res + " link(s)")
      Redirect(routes.Application.index())
    }
  }
}
