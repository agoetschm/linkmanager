package modules

import com.google.inject.AbstractModule
import models.daos.{AuthTokenDAO, AuthTokenDAOImpl, LinkDAO, LinkDAOImpl}
import models.services.{AuthTokenService, AuthTokenServiceImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * The base Guice module
  */
class BaseModule extends AbstractModule with ScalaModule{
  
  override def configure(): Unit = {
    bind[LinkDAO].to[LinkDAOImpl]
    
    bind[AuthTokenDAO].to[AuthTokenDAOImpl]
    bind[AuthTokenService].to[AuthTokenServiceImpl]
  }
}
