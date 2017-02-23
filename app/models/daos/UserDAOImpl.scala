package models.daos


import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable
import scala.concurrent.Future

/**
  * User dao implementation
  */
class UserDAOImpl

//@Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserDAO {

  //    with HasDatabaseConfigProvider[JdbcProfile] {

  //  import dbConfig.driver.api._

  override def find(loginInfo: LoginInfo): Future[Option[User]] =
    Future.successful(UserDAOImpl.users.get(1))
}

object UserDAOImpl {

  /**
    * The list of users.
    */
  val users: mutable.HashMap[Long, User] = mutable.HashMap()
  users.put(1, User(1, "a", new BCryptPasswordHasher().hash("b").password))
}

