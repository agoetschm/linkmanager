package controllers

import com.google.inject.Inject
import models.daos.LinkDAO
import models.{Link, LinkForm, UserLogInForm}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Log in controller
  */
class LogIn @Inject()
(val linkDAO: LinkDAO, val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def view = Action { implicit request =>
    Ok(views.html.login(UserLogInForm.form))
  }

  def submit = Action.async { implicit request =>
    Future.successful(Redirect(routes.Application.index()))
//    LinkForm.form.bindFromRequest.fold(
//      errorForm => {
//        Logger.warn("error : " + errorForm.errors)
//        Future.successful(Ok(views.html.index(errorForm, Seq.empty)))
//      },
//      successData => {
//        linkDAO.add(Link(id = 0,
//          url = successData.url,
//          name = successData.name,
//          description = successData.description,
//          screenshot = Some(Array.emptyByteArray)) // TODO None does not work
//        ) map {
//          newId =>
//            Logger.debug("new link with id " + newId)
//            Redirect(routes.Application.index())
//        }
//      }
//    )
  }

}
