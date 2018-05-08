lazy val akkaHttpVersion = "10.1.1"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "golang",
      scalaVersion := "2.12.4"
    )),
    name := "go-as-function",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"           % akkaHttpVersion,
      "de.heikoseeberger" %% "akka-http-play-json" % "1.20.1",

      "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"           % "3.0.5" % Test
    )
  )

scalacOptions := Seq(
  "-encoding", "UTF-8",
  "-target:jvm-1.8",
  "-Ywarn-infer-any",
  "-Ywarn-dead-code",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-Ywarn-value-discard",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-g:vars",
  "-Xlint:_"
)
