package modules

import com.google.inject.AbstractModule
import models.{Folder, Link}
import models.daos._
import models.services.{AuthTokenService, AuthTokenServiceImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * The base Guice module
  */
class BaseModule extends AbstractModule with ScalaModule{
  
  override def configure(): Unit = {
    bind[EntityDAO[Link]].to[LinkDAOImpl]
    bind[EntityDAO[Folder]].to[FolderDAOImpl]
    
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
  }
}
