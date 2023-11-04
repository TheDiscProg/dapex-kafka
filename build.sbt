import scala.collection.Seq

ThisBuild / organization := "DAPEX"

ThisBuild / version := "0.2.3"

lazy val commonSettings = Seq(
  scalaVersion := "2.13.10",
  libraryDependencies ++= Dependencies.all,
  resolvers += Resolver.githubPackages("TheDiscProg"),
  githubOwner := "TheDiscProg",
  githubRepository := "dapex-kafka",
  addCompilerPlugin(
    ("org.typelevel" %% "kind-projector" % "0.13.2").cross(CrossVersion.full)
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
)

lazy val root = project.in(file("."))
  .enablePlugins(
    ScalafmtPlugin,
    JavaAppPackaging
  )
  .settings(
    commonSettings,
    name := "dapex-kafka",
    scalacOptions ++= Scalac.options
  )

lazy val integrationTest = (project in file ("it"))
  .enablePlugins(ScalafmtPlugin)
  .settings(
    commonSettings,
    name := "dapex-kafka-integration-test",
    publish / skip := true,
    libraryDependencies ++= Dependencies.it,
    parallelExecution := true
  )
  .dependsOn(root % "test->test; compile->compile")
  .aggregate(root)

githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")

addCommandAlias("formatAll", ";scalafmt;test:scalafmt;integrationTest/test:scalafmt;")
addCommandAlias("cleanAll", ";clean;integrationTest:clean")
addCommandAlias("itTest", ";integrationTest/test")
addCommandAlias("testAll", ";cleanAll;formatAll;test;itTest;")
