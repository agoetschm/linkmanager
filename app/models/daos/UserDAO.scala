package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future

/**
  * User dao interface
  */
trait UserDAO {
  def find(loginInfo: LoginInfo): Future[Option[User]]

}
