import com.typesafe.sbt.packager.universal.UniversalPlugin
import sbt.Keys.{version, _}
import sbt._
import sbtrelease.{ReleaseStateTransformations, Version}

object Release {

  import sbtrelease.ReleasePlugin.autoImport._

  import sbtrelease._
  // we hide the existing definition for setReleaseVersion to replace it with our own
  import sbtrelease.ReleaseStateTransformations.{setReleaseVersion=>_,_}

  def setVersionOnly(selectVersion: Versions => String): ReleaseStep =  { st: State =>
    val vs = st.get(ReleaseKeys.versions).getOrElse(sys.error("No versions are set! Was this release part executed before inquireVersions?"))
    val selected = selectVersion(vs)

    st.log.info("Setting version to '%s'." format selected)
    val useGlobal =Project.extract(st).get(releaseUseGlobalVersion)
    val versionStr = (if (useGlobal) globalVersionString else versionString) format selected

    reapply(Seq(
      if (useGlobal) version in ThisBuild := selected
      else version := selected
    ), st)
  }

  releaseVersion <<= (releaseVersionBump)( bumper=>{
    ver => Version(ver)
      .map(_.withoutQualifier)
      .map(_.bump(bumper).string).getOrElse(versionFormatError)
  })

  lazy val setReleaseVersion: ReleaseStep = setVersionOnly(_._1)

  private val releaseMaster = Def.setting {
    Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      setReleaseVersion,
      runTest,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      // publishArtifacts,
      ReleaseStep(releaseStepTask(publish in UniversalPlugin.autoImport.Universal)),
      pushChanges
      //releaseStepCommand(ExtraReleaseCommands.initialVcsChecksCommand),
    )
  }

  val settings = Seq(
    releaseCommitMessage := s"Set version to ${(version in ThisBuild).value}",
    releaseTagName := (version in ThisBuild).value,
    publishTo := Some("temp" at "file:///tmp/repository"),
    releaseProcess := releaseMaster.value
  )
}
