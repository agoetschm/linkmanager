package models.daos

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by agoetschm on 2/23/17.
  */
class PasswordInfoDAOImpl @Inject()(userDAO: UserDAO) extends DelegableAuthInfoDAO[PasswordInfo] {
  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] =
    userDAO.find(loginInfo).map {
      case Some(user) => Some(PasswordInfo(BCryptPasswordHasher.ID, user.password, salt = Some("salt")))
      case _ => None
    }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???
}
