import sbt._

object Dependencies {

  private lazy val simexVersion = "0.9.3"
  private lazy val circeVersion = "0.14.10"
  private lazy val enumeratumVersion = "1.7.5"
  private lazy val catsEffectVersion = "3.5.4"
  private lazy val fs2KafkaVersion = "3.5.1"
  private lazy val scaffeineVersion = "5.3.0"
  private lazy val logbackClassicVersion = "1.5.9"

  lazy val all = Seq(
    "io.github.thediscprog" %% "simex-messaging" % simexVersion,
    "io.github.thediscprog" %% "simex-util-library" % "0.9.3",
    "com.github.fd4s" %% "fs2-kafka" % fs2KafkaVersion,
    "com.beachape" %% "enumeratum" % enumeratumVersion,
    "com.beachape" %% "enumeratum-circe" % enumeratumVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "org.typelevel" %% "log4cats-slf4j" % "2.7.0",
    "org.scalatest" %% "scalatest" % "3.2.19" % Test,
    "org.typelevel" %% "cats-effect-testing-scalatest" % "1.5.0" % Test,
    "ch.qos.logback" % "logback-classic" % logbackClassicVersion % Test,
    "com.github.blemale" %% "scaffeine" % scaffeineVersion % Test
  )

  lazy val it = Seq(
    "ch.qos.logback" % "logback-classic" % logbackClassicVersion % Test,
    "org.testcontainers" % "kafka" % "1.20.2" % Test
  )
}