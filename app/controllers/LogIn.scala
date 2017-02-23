package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.{LoginEvent, Silhouette}
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.UserLogInForm
import models.services.UserService
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Log in controller
  */
class LogIn @Inject()(
                       silhouette: Silhouette[DefaultEnv],
                       userService: UserService,
                       credentialsProvider: CredentialsProvider,
                       val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def view = Action { implicit request =>
    Ok(views.html.login(UserLogInForm.form))
  }

  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request =>
    UserLogInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.login(form))),
      data => {
        val credentials = Credentials(data.username, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val result = Redirect(routes.Application.index())
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              silhouette.env.authenticatorService.create(loginInfo).flatMap {
                authenticator =>
                  silhouette.env.eventBus.publish(LoginEvent(user, request))
                  silhouette.env.authenticatorService.init(authenticator).flatMap { v =>
                    silhouette.env.authenticatorService.embed(v, result)
                  }
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            Redirect(routes.LogIn.view()).flashing("error" -> "Invalid credentials")
        }
      }
    )
  }

}
