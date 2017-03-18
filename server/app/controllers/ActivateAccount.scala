package controllers

import java.net.URLDecoder
import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.daos.UserDAO
import models.services.{AuthTokenService, UserService}
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.Controller
import utils.auth.DefaultEnv

import scala.concurrent.Future
import scala.language.postfixOps

/**
 * The `Activate Account` controller.
 *
 * @param messagesApi      The Play messages API.
 * @param silhouette       The Silhouette stack.
 * @param userDAO      The user dao implementation.
 * @param authTokenService The auth token service implementation.
 * @param mailerClient     The mailer client.
 * @param webJarAssets     The WebJar assets locator.
 */
class ActivateAccount @Inject()(
  val messagesApi: MessagesApi,
  silhouette: Silhouette[DefaultEnv],
  userDAO: UserDAO,
  authTokenService: AuthTokenService,
  mailerClient: MailerClient
//  ,
//  implicit val webJarAssets: WebJarAssets
                                         )
  extends Controller with I18nSupport {

  /**
   * Sends an account activation email to the user with the given email.
   *
   * @param email The email address of the user to send the activation mail to.
   * @return The result to display.
   */
  def send(email: String) = silhouette.UnsecuredAction.async { implicit request =>
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val result = Redirect(routes.LogIn.view()).flashing("info" -> ("Activation email sent to " + decodedEmail) /*Messages("activation.email.sent", decodedEmail)*/)

    userDAO.findByEmail(decodedEmail).flatMap {
      case Some(user) if !user.activated =>
        authTokenService.create(user.id).map { authToken =>
          val url = routes.ActivateAccount.activate(authToken.id).absoluteURL()

          mailerClient.send(Email(
            subject = Messages("email.activate.account.subject"),
            from = Messages("email.from"),
            to = Seq(decodedEmail),
            bodyText = Some(views.txt.emails.activateAccount(user, url).body),
            bodyHtml = Some(views.html.emails.activateAccount(user, url).body)
          ))
          result
        }
      case _ => Future.successful(result) // prevents that anybody can try email and see if they are registered
    }
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: Long) = silhouette.UnsecuredAction.async { implicit request =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userDAO.find(authToken.userID).flatMap {
        case Some(user) /* if user.loginInfo.providerID == CredentialsProvider.ID */ =>
          Logger.debug("activate")
          userDAO.update(user.copy(activated = true)).map { _ =>
            // TODO may fail
            Redirect(routes.LogIn.view()).flashing("success" -> Messages("account.activated"))
          }
        case _ =>
          Logger.debug("fail : no user")
          Future.successful(Redirect(routes.LogIn.view()).flashing("error" -> Messages("invalid.activation.link")))
      }
      case None =>
        Logger.debug("fail : no auth token")
        Future.successful(Redirect(routes.LogIn.view()).flashing("error" -> Messages("invalid.activation.link")))
    }
  }
}
