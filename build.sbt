
name := "linkmanager"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

val webjars = Seq(
  "org.webjars.bower" % "jquery" % "3.1.0",
  "org.webjars.bower" % "materialize" % "0.97.6"
)

libraryDependencies ++= Seq(
  //jdbc,
  //anorm,
  cache,
  ws,
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
  "com.iheart" %% "ficus" % "1.4.0"
) ++ webjars

resolvers += Resolver.jcenterRepo
resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
