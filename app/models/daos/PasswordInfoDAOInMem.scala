package models.daos

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.exceptions.AuthenticatorCreationException
import com.mohiva.play.silhouette.api.util.{PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import PasswordInfoDAOInMem._

/**
  * PasswordInfo dao impl
  */
class PasswordInfoDAOInMem @Inject()(userDAO: UserDAO, passwordHasherRegistry: PasswordHasherRegistry)
  extends DelegableAuthInfoDAO[PasswordInfo] {

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    userDAO.find(loginInfo).map {
      case Some(user) => passwords.find { case (id, pass) => id == user.id }.map(_._2)
      case None => None
    }
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    userDAO.find(loginInfo).map {
      case Some(user) =>
        passwords += (user.id -> authInfo)
        authInfo
      case None => throw new AuthenticatorCreationException("no user for loginInfo " + loginInfo)
    }

  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???
}

object PasswordInfoDAOInMem {
  val passwords: mutable.HashMap[Long, PasswordInfo] = mutable.HashMap()
  passwords.put(1, new BCryptPasswordHasher().hash("b"))
}
