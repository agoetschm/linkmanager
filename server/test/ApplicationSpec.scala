import controllers.Application
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.Future

class ApplicationSpec /*@Inject()(silhouette: Silhouette[DefaultEnv],
                                linkDAO: LinkDAO,
                                val messagesApi: MessagesApi)*/ extends PlaySpec with Results {

  "Example Page#index" should {
    "should be valid" in {
      val controller: Application = ??? //new Application(silhouette, linkDAO, messagesApi)
      val result: Future[Result] = controller.index().apply(FakeRequest())
      val bodyText: String = contentAsString(result)
      bodyText mustBe "ok"
    }
  }


  //  /**
  //    * The context.
  //    */
  //  trait Context extends Scope {
  //
  //    /**
  //      * A fake Guice module.
  //      */
  //    class FakeModule extends AbstractModule with ScalaModule {
  //      def configure() = {
  //        bind[Environment[DefaultEnv]].toInstance(env)
  //      }
  //    }
  //
  //    /**
  //      * An identity.
  //      */
  //    val identity = User(
  //      0L,
  //      "username",
  //      "a@b.c",
  //      activated = true
  //    )
  //
  //    /**
  //      * A Silhouette fake environment.
  //      */
  //    implicit val env: Environment[DefaultEnv] = new FakeEnvironment[DefaultEnv](
  //      Seq(LoginInfo(CredentialsProvider.ID, identity.username) -> identity))
  //
  //
  //    /**
  //      * The application.
  //      */
  //    lazy val application = new GuiceApplicationBuilder()
  //      .overrides(new FakeModule)
  //      .build()
  //  }
}