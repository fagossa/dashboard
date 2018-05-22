lazy val AkkaHttpVersion = "10.1.1"
lazy val ScalaTestVersion = "3.0.5"
lazy val LogbackVersion = "1.2.3"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin, WebpackPlugin, JavaAppPackaging, GitVersioning, GitBranchPrompt)
  .settings(
    inThisBuild(List(
      organization := "cbio",
      scalaVersion := "2.12.4"
    )),
    name := "app-template",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"           % AkkaHttpVersion,
      "de.heikoseeberger" %% "akka-http-play-json" % "1.20.1",

      // logs
      "ch.qos.logback"    %  "logback-classic"     % LogbackVersion ,
      "com.typesafe.akka" %% "akka-slf4j"          % "2.5.2",

      // test
      "com.typesafe.akka" %% "akka-http-testkit"   % AkkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"           % ScalaTestVersion % Test
    )
  )
  .settings(BuildTools.buildInfo.settings)
  .settings(BuildTools.gitRelease.settings)
  .settings(BuildTools.fmt.settings)
  .settings(BuildTools.common.settings)
  .settings(Release.settings)
  .settings(
    fork in run := true,
    fork in Test := true,
    fork in IntegrationTest := true
  )

val showNextVersion = settingKey[String]("the future version once releaseNextVersion has been applied to it")
val showReleaseVersion = settingKey[String]("the future version once releaseNextVersion has been applied to it")
showReleaseVersion <<= (version, releaseVersion)((v,f)  => f(v))
showNextVersion <<= (version, releaseNextVersion)((v,f) => f(v))

// native-packager
mappings in (Compile, packageDoc) := Seq()
