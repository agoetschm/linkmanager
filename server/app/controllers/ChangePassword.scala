package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.forms.ChangePasswordForm
import models.services.UserService
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.Controller
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Change password controller.
  *
  * @param messagesApi            The Play messages API.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param credentialsProvider    The credentials provider.
  * @param authInfoRepository     The auth info repository.
  * @param passwordHasherRegistry The password hasher registry.
  */
class ChangePassword @Inject()(
                                val messagesApi: MessagesApi,
                                silhouette: Silhouette[DefaultEnv],
                                userService: UserService,
                                credentialsProvider: CredentialsProvider,
                                authInfoRepository: AuthInfoRepository,
                                passwordHasherRegistry: PasswordHasherRegistry)
  extends Controller with I18nSupport {

  /**
    * Change password page
    *
    * @return The result to display.
    */
  def view = silhouette.SecuredAction /*(WithProvider[DefaultEnv#A](CredentialsProvider.ID))*/ { implicit request =>
    Ok(views.html.changePassword(ChangePasswordForm.form, request.identity))
  }

  /**
    * Changes the password
    *
    * @return The result to display.
    */
  def submit = silhouette.SecuredAction /*(WithProvider[DefaultEnv#A](CredentialsProvider.ID))*/ .async { implicit request =>
    ChangePasswordForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.changePassword(form, request.identity))),
      password => {
        val (currentPassword, newPassword) = password
        val credentials = Credentials(request.identity.username, currentPassword)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
          authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
            Redirect(routes.ChangePassword.view()).flashing("success" -> Messages("password.changed"))
          }
        }.recover {
          case e: ProviderException =>
            Redirect(routes.ChangePassword.view()).flashing("error" -> Messages("current.password.invalid"))
        }
      }
    )
  }
}
