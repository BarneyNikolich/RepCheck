name := """Repcheck"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"


libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
  jdbc,
  cache,
  ws,
  evolutions,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.typesafe.play" %% "anorm" % "2.5.0",
  "org.mindrot" % "jbcrypt" % "0.3m"

)


