name := "linkmanager"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val webjars = Seq(
  "org.webjars.bower" % "jquery" % "3.1.0",
  //  "org.webjars.bower" % "materialize" % "0.97.6"
  "org.webjars" % "materializecss" % "0.97.5"
  //  "org.webjars" % "material-design-icons" % "2.2.0"
)

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "mysql" % "mysql-connector-java" % "5.1.34"
) ++ webjars

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
