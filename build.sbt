lazy val akkaHttpVersion = "10.1.1"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin, WebpackPlugin, JavaAppPackaging, GitVersioning, GitBranchPrompt)
  .settings(
    inThisBuild(List(
      organization := "cbio",
      scalaVersion := "2.12.4"
    )),
    name := "app-template",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"           % akkaHttpVersion,
      "de.heikoseeberger" %% "akka-http-play-json" % "1.20.1",
      // logs
      "ch.qos.logback"    %  "logback-classic"     % "1.2.3" ,
      "com.typesafe.akka" %% "akka-slf4j"          % "2.5.2",
      // test
      "com.typesafe.akka" %% "akka-http-testkit"   % akkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"           % "3.0.5" % Test
    )
  )
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "org.fabian.build"
  )
  .settings(
    git.useGitDescribe := true,
    git.baseVersion := "0.0.1",
    git.gitTagToVersionNumber := {
      case VersionRegex(v,"") => Some(v)
      case VersionRegex(v,"SNAPSHOT") => Some(s"$v-SNAPSHOT")
      case VersionRegex(v,s) => Some(s"$v-$s-SNAPSHOT")
      case _ => None
    }
  )
  .settings(Release.settings)

/* Accepted version format
 * 0.0.0-SNAPSHOT
 * 0.0.0-xxxxx-SNAPSHOT //with xxxxxx a SHA-1
 * 1.0.0 // for a commit whose SHA-1 has been tagged with v1.0.0
 * 1.0.0-2-yyyyy-SNAPSHOT // for the second commit after the tag
 */
val VersionRegex = "v([0-9]+.[0-9]+.[0-9]+)-?(.*)?".r

val showNextVersion = settingKey[String]("the future version once releaseNextVersion has been applied to it")
val showReleaseVersion = settingKey[String]("the future version once releaseNextVersion has been applied to it")
showReleaseVersion <<= (version, releaseVersion)((v,f)  => f(v))
showNextVersion <<= (version, releaseNextVersion)((v,f) => f(v))

// native-packager
mappings in (Compile, packageDoc) := Seq()

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
