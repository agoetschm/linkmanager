package models.daos

import akka.actor.Status.Success
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.{PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import models.{Password, User}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.mohiva.play.silhouette.api.exceptions.AuthenticatorCreationException
import models.tables.{PasswordTableDef, UserTableDef}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * PasswordInfo dao impl
  */
class PasswordInfoDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                passwordHasherRegistry: PasswordHasherRegistry)
  extends DelegableAuthInfoDAO[PasswordInfo] with HasDatabaseConfigProvider[JdbcProfile] {

  import dbConfig.driver.api._

  val users = TableQuery[UserTableDef]
  val passwords = TableQuery[PasswordTableDef]

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    val usersWithPassword = for {
      (u, p) <- users join passwords on (_.id === _.userId)
    } yield (u.id, u.username, p.password)

    def userHavingUsername(username: String) = usersWithPassword.filter(
      _._2 === username)

    val password = userHavingUsername(loginInfo.providerKey).map(_._3).result.headOption
    val authInfo = password.map {
      case Some(password) => Some(PasswordInfo(passwordHasherRegistry.current.id, password))
      case None => None
    }

    db.run(authInfo)
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {

    // I haven't found a way to compose DBIOAction here
    //    http://tastefulcode.com/2015/03/19/modern-database-access-scala-slick/

    val maybeUserid = db.run(
      users.filter(_.username === loginInfo.providerKey).map(_.id).result.headOption)
    val password = maybeUserid.flatMap {
      case Some(userId) => db.run(
        passwords returning passwords.map(_.password) += Password(userId, authInfo.password))
      case None => throw new Exception("no user for loginInfo " + loginInfo)
    }
    password.map(PasswordInfo(passwordHasherRegistry.current.id, _))
      .recover {
        case e: Exception => throw new Exception("failed to create password " + authInfo)
      }
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    val userWithUsername = users.filter(_.username === loginInfo.providerKey).result.headOption
    val update = userWithUsername.flatMap {
      case None => throw new Exception("no user for loginInfo " + loginInfo)
      case Some(user) =>
        val password: Password = Password(user.id, authInfo.password)
        passwords.filter(_.userId === user.id).update(password)
    }
    db.run(update).map {
      case 0 => throw new Exception("failed to update password")
      case _ => authInfo
    }
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = ???

  override def remove(loginInfo: LoginInfo): Future[Unit] = ???
}
