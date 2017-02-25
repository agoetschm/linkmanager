package models.services
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.daos.UserDAO

import scala.concurrent.Future

/**
  * Created by agoetschm on 2/21/17.
  */
class UserServiceImpl @Inject() (userDAO: UserDAO) extends UserService{
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.find(loginInfo)

  override def create(user: User): Future[User] = userDAO.add(user)
}
