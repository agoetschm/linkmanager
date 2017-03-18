package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.{LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import models.daos.UserDAO
import models.forms.UserSignUpForm
import models.services.AuthTokenService
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Sign up controller
  */
class SignUp @Inject()(
                        silhouette: Silhouette[DefaultEnv],
                        userDAO: UserDAO,
                        authInfoRepository: AuthInfoRepository,
                        passwordHasherRegistry: PasswordHasherRegistry,
                        authTokenService: AuthTokenService,
                        mailerClient: MailerClient,
                        val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def view = silhouette.UnsecuredAction { implicit request =>
    Ok(views.html.signup(UserSignUpForm.form))
  }

  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request =>
    UserSignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signup(form))),
      data => {
        userDAO.find(data.username).flatMap {
          case Some(user) => Future.successful(Redirect(routes.SignUp.view())
            .flashing("error" -> Messages("username.taken")))
          case None =>
            val loginInfo = LoginInfo(CredentialsProvider.ID, data.username)
            val authInfo = passwordHasherRegistry.current.hash(data.password)
            val user = User(
              id = 0 /* will be set at creation */ , 
              username = data.username, 
              email = "test@example.com", // TODO
              activated = false
             )
            for {
              user <- userDAO.add(user)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
              authToken <- authTokenService.create(user.id)
            } yield {
              Logger.debug("auth token : " + authToken)

              val url = routes.ActivateAccount.activate(authToken.id).absoluteURL()
              mailerClient.send(Email(
                subject = Messages("email.sign.up.subject"),
                from = Messages("email.from"),
//                to = Seq(data.email),
                to = Seq(user.email),
                bodyText = Some(views.txt.emails.signUp(user, url).body),
                bodyHtml = Some(views.html.emails.signUp(user, url).body)
              ))
              
              
              Redirect(routes.SignUp.view()).flashing("info" -> Messages("sign.up.email.sent", user.email))
            }
        }
      }
    )
  }

}
