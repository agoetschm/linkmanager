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
import UserDAOImpl._

/**
  * User dao implementation
  */
class UserDAOImpl

//@Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserDAO {

  //    with HasDatabaseConfigProvider[JdbcProfile] {

  //  import dbConfig.driver.api._

  override def find(loginInfo: LoginInfo): Future[Option[User]] =
    Future.successful(users
      .find { case (id, user) => user.username == loginInfo.providerKey }
      .map(_._2))

  override def save(user: User): Future[User] = {
    maxId += 1
    val newUser = User(maxId, user.username)
    users += (maxId -> newUser)
    Future.successful(newUser)
  }
}

object UserDAOImpl {

  /**
    * The list of users.
    */
  val users: mutable.HashMap[Long, User] = mutable.HashMap()
  users.put(1, User(1, "a"))
  var maxId: Long = 1L
}

