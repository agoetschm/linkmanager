
name := "linkmanager"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

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
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "org.postgresql" % "postgresql" % "9.4-1202-jdbc41",
  "com.mohiva" %% "play-silhouette" % "4.0.0",
  "net.codingwell" %% "scala-guice" % "4.1.0"
) ++ webjars

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
