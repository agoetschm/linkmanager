val scalaV = "2.11.8"


val webjars = Seq(
  //  "org.webjars.bower" % "jquery" % "3.1.0",
  //  "org.webjars.bower" % "materialize" % "0.97.6"
)

lazy val server = (project in file("server")).settings(
  scalaVersion := scalaV,
  scalaJSProjects := Seq(client),
  pipelineStages in Assets := Seq(scalaJSPipeline),
  pipelineStages := Seq(digest, gzip),
  // triggers scalaJSPipeline when using compile or continuous compilation
  compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
  libraryDependencies ++= Seq(
    //jdbc,
    //anorm,
    cache,
    ws,
    filters,
    "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,

    // database
    "com.typesafe.play" %% "play-slick" % "2.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
    "org.postgresql" % "postgresql" % "9.4-1202-jdbc41",

    // silhouette
    "com.mohiva" %% "play-silhouette" % "4.0.0",
    "com.mohiva" %% "play-silhouette-password-bcrypt" % "4.0.0",
    "com.mohiva" %% "play-silhouette-crypto-jca" % "4.0.0",
    "com.mohiva" %% "play-silhouette-persistence" % "4.0.0",
    "com.mohiva" %% "play-silhouette-testkit" % "4.0.0" % "test",

    // from the play-silhouette-seed
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "com.iheart" %% "ficus" % "1.4.0",
    "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.0-akka-2.4.x",
    "com.typesafe.play" %% "play-mailer" % "5.0.0",

    // include scala-js client sources in twirl template
    "com.vmunier" %% "scalajs-scripts" % "1.0.0"
  ) ++ webjars
).enablePlugins(PlayScala).
  dependsOn(sharedJvm)


lazy val client = (project in file("client")).settings(
  scalaVersion := scalaV,
  persistLauncher := true,
  persistLauncher in Test := false,
  mainClass in Compile := Some("MainApp"),
  libraryDependencies ++= Seq(
    "be.doeraene" %%% "scalajs-jquery" % "0.9.1",
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "com.lihaoyi" %%% "scalatags" % "0.6.3"
  ),
  skip in packageJSDependencies := false,
  jsDependencies +=
    "org.webjars" % "jquery" % "3.1.1" / "3.1.1/jquery.js"
).enablePlugins(ScalaJSPlugin, ScalaJSWeb).
  dependsOn(sharedJs)


lazy val shared = (crossProject.crossType(CrossType.Pure) in file("shared"))
  .settings(
    scalaVersion := scalaV,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.4.3"
    ))
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

// loads the server project at sbt startup
onLoad in Global := (Command.process("project server", _: State)) compose (onLoad in Global).value


resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
resolvers in server += "JBoss" at "https://repository.jboss.org/"
resolvers in server += Resolver.jcenterRepo
//resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"