package utils.auth

import com.google.inject.{Inject, Provider}
import com.mohiva.play.silhouette.api.{Authenticator, Authorization}
import models.User
import models.daos.LinkDAO
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Grants only access if the actual user is the owner of the following link
  *
  * @param linkDAO The DAO for links
  * @param linkId  The id of the link
  * @tparam A The type of the authenticator
  */
case class BeingOwnerOf[A <: Authenticator](linkId: Long)(linkDAO: LinkDAO)
  extends Authorization[User, A] {
  // TODO https://github.com/google/guice/wiki/AssistedInject

  /**
    * Verifies if the user is the owner of a link
    *
    * @param user          The current user
    * @param authenticator The instance of the Authenticator
    * @param request       The request
    * @tparam B The type of the request body
    * @return Whether the user is the owner or not
    */
  override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]) = {
    linkDAO.get(linkId).map {
      case Some(link) => link.userId == user.id
      case None => false
    }
  }
}