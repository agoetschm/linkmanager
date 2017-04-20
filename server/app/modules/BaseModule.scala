package modules

import com.google.inject.AbstractModule
import models.daos._
import models.services.{AuthTokenService, AuthTokenServiceImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * The base Guice module
  */
class BaseModule extends AbstractModule with ScalaModule{
  
  override def configure(): Unit = {
    bind[LinkDAO].to[LinkDAOImpl]
    bind[FolderDAO].to[FolderDAOImpl]
    
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
  }
}
