import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport.{scalafmtOnCompile, scalafmtVersion}
import com.lucidchart.sbt.scalafmt.ScalafmtSbtPlugin.autoImport.Sbt
import com.typesafe.sbt.GitPlugin.autoImport.git
import sbt.Keys._
import sbtbuildinfo.BuildInfoPlugin.autoImport.{BuildInfoKey, buildInfoKeys, buildInfoPackage}

object BuildTools {

  object buildInfo {
    lazy val settings =
      Seq(
        buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
        buildInfoPackage := "org.fabian.build"
      )
  }

  object gitRelease {
    val VersionRegex = "v([0-9]+.[0-9]+.[0-9]+)-?(.*)?".r

    lazy val settings =
      Seq(
        git.useGitDescribe := true,
        git.baseVersion := "0.0.1",
        git.gitTagToVersionNumber := {
          case VersionRegex(v,"") => Some(v)
          case VersionRegex(v,"SNAPSHOT") => Some(s"$v-SNAPSHOT")
          case VersionRegex(v,s) => Some(s"$v-$s-SNAPSHOT")
          case _ => None
        }
      )
  }

  object fmt {
    lazy val settings =
      Seq(
        scalafmtOnCompile := true,
        scalafmtOnCompile.in(Sbt) := false,
        scalafmtVersion := "1.3.0"
      )
  }

  object common {
    lazy val settings =
      Seq(
        scalacOptions := Seq(
          "-encoding", "UTF-8",
          "-target:jvm-1.8",
          "-Ywarn-infer-any",
          "-Ywarn-dead-code",
          "-Ywarn-unused",
          "-Ywarn-unused-import",
          "-Ywarn-value-discard",
          "-Ypartial-unification",
          "-unchecked",
          "-deprecation",
          "-feature",
          "-g:vars",
          "-Xlint:_"
        )
      )
  }

}
