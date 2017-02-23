package models.services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User

import scala.concurrent.Future

/**
  * Created by agoetschm on 2/21/17.
  */
trait UserService extends IdentityService[User] {
}
