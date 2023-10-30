import sbt._

object Dependencies {

  lazy val all = Seq(
    "DAPEX" %% "dapex-messaging" % "0.2.0",
    "thediscprog" %% "util-library" % "0.1.1",
    "com.github.fd4s" %% "fs2-kafka" % "3.2.0",
    "com.beachape" %% "enumeratum" % "1.7.2",
    "com.beachape" %% "enumeratum-circe" % "1.7.2",
    "io.circe" %% "circe-core" % "0.14.5",
    "io.circe" %% "circe-generic" % "0.14.5",
    "io.circe" %% "circe-parser" % "0.14.5",
    "io.circe" %% "circe-refined" % "0.14.5",
    "io.circe" %% "circe-generic-extras" % "0.14.3",
    "org.typelevel" %% "cats-effect" % "3.4.8",
    "org.typelevel" %% "log4cats-slf4j" % "2.6.0",
    "org.scalatest" %% "scalatest" % "3.2.15" % "test",
    "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % "test",
    "ch.qos.logback" % "logback-classic" % "1.4.11" % "test",
    "com.github.blemale" %% "scaffeine" % "5.2.1" % "test"
  )

  lazy val it = Seq(
    "ch.qos.logback" % "logback-classic" % "1.4.11",
    "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.41.0",
    "org.testcontainers" % "kafka" % "1.19.1"
  )
}