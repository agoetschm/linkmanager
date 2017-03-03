package models.daos


import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.exceptions.AuthenticatorCreationException
import models.User
import models.tables.UserTableDef
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * User dao implementation
  */
class UserDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends UserDAO with HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.driver.api._

  val users = TableQuery[UserTableDef]

  override def find(loginInfo: LoginInfo): Future[Option[User]] =
    dbConfig.db.run(users.filter(_.username === loginInfo.providerKey).result.headOption)


  // TODO handle exceptions (-> test)
  override def add(user: User): Future[User] =
    dbConfig.db.run(users returning users += user)
      .recover {
        case e: Exception => throw new AuthenticatorCreationException("failed to create user " + user)
      }

}