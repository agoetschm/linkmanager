package utils.auth

import com.mohiva.play.silhouette.api.{Authenticator, Authorization}
import models.daos.EntityDAO
import models.{Entity, User}
import play.api.mvc.Request

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Grants only access if the actual user is the owner of the following entity
  *
  * @param dao The needed DAO 
  * @param id  The id of the entity
  * @tparam A The type of the authenticator
  * @tparam E The type of the entity
  */
case class BeingOwnerOf[A <: Authenticator, E <: Entity](id: Long)(dao: EntityDAO[E])
  extends Authorization[User, A] {
  // TODO https://github.com/google/guice/wiki/AssistedInject

  /**
    * Verifies if the user is the owner of an entity
    *
    * @param user          The current user
    * @param authenticator The instance of the Authenticator
    * @param request       The request
    * @tparam B The type of the request body
    * @return Whether the user is the owner or not
    */
  override def isAuthorized[B](user: User, authenticator: A)(implicit request: Request[B]) = {
    dao.get(id).map {
      case Some(e) => e.userId == user.id
      case None => false
    }
  }
}