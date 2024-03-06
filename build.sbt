ThisBuild / organization := "simex"

ThisBuild / version := "0.8.0" //Keep this at the same version as simex-messaging

lazy val commonSettings = Seq(
  scalaVersion := "2.13.10",
  libraryDependencies ++= Dependencies.all,
  resolvers += Resolver.githubPackages("TheDiscProg"),
  githubOwner := "TheDiscProg",
  githubRepository := "simex-kafka",
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
    name := "simex-kafka",
    scalacOptions ++= Scalac.options
  )

lazy val integrationTest = (project in file ("it"))
  .enablePlugins(ScalafmtPlugin)
  .settings(
    commonSettings,
    name := "simex-kafka-integration-test",
    publish / skip := true,
    libraryDependencies ++= Dependencies.it,
    parallelExecution := false,
    coverageFailOnMinimum := true,
    coverageMinimumStmtTotal := 85,
    coverageMinimumBranchTotal := 80,
  )
  .dependsOn(root % "test->test; compile->compile")
  .aggregate(root)

githubTokenSource := TokenSource.Environment("GITHUB_TOKEN")

addCommandAlias("formatAll", ";scalafmt;test:scalafmt;integrationTest/test:scalafmt;")
addCommandAlias("cleanAll", ";clean;integrationTest/clean")
addCommandAlias("itTest", ";integrationTest/clean;integrationTest/test:scalafmt;integrationTest/test")
addCommandAlias("cleanTest", ";clean;scalafmt;test:scalafmt;test;")
addCommandAlias("testAll", ";cleanAll;formatAll;test;itTest;")
addCommandAlias("cleanCoverage", ";cleanAll;formatAll;coverage;testAll;coverageReport;")
