package modules

import com.google.inject.AbstractModule
import models.daos.{LinkDAO, LinkDAOImpl}
import net.codingwell.scalaguice.ScalaModule

/**
  * The base Guice module
  */
class BaseModule extends AbstractModule with ScalaModule{
  
  override def configure(): Unit = {
    bind[LinkDAO].to[LinkDAOImpl]
  }
}
