package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, Silhouette}
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import models.forms.UserSignUpForm
import models.services.UserService
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import utils.auth.DefaultEnv

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

/**
  * Sign up controller
  */
class SignUp @Inject()(
                        silhouette: Silhouette[DefaultEnv],
                        userService: UserService,
                        authInfoRepository: AuthInfoRepository,
                        passwordHasherRegistry: PasswordHasherRegistry,
                        val messagesApi: MessagesApi)
  extends Controller with I18nSupport {

  def view = silhouette.UnsecuredAction { implicit request =>
    Ok(views.html.signup(UserSignUpForm.form))
  }

  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request =>
    UserSignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.signup(form))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.username)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) => Future.successful(Redirect(routes.SignUp.view())
            .flashing("error" -> "Username already exists"))
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(data.password)
            val user = User(0 /* will be set at creation */ , data.username)
            for {
              user <- userService.create(user)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)              
            } yield {
              Redirect(routes.SignUp.view()).flashing("info" -> ("Successfully signed up !"))
            }
        }
      }
    )
  }

}
