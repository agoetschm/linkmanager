package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.collection.mutable
import scala.concurrent.Future

import UserDAOInMemImpl._

/**
  * User dao implementation
  */
class UserDAOInMemImpl

//@Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserDAO {

  //    with HasDatabaseConfigProvider[JdbcProfile] {

  //  import dbConfig.driver.api._

  override def find(loginInfo: LoginInfo): Future[Option[User]] =
    Future.successful(users
      .find { case (id, user) => user.username == loginInfo.providerKey }
      .map(_._2))

  override def add(user: User): Future[User] = {
    maxId += 1
    val newUser = User(maxId, user.username)
    users += (maxId -> newUser)
    Future.successful(newUser)
  }
}

object UserDAOInMemImpl {

  /**
    * The list of users.
    */
  val users: mutable.HashMap[Long, User] = mutable.HashMap()
  users.put(1, User(1, "a"))
  var maxId: Long = 1L
}

