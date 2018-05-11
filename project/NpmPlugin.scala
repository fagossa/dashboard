import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.{Universal, stage}
import sbt.Keys.{TaskStreams, streams, thisProject}
import sbt._

import scala.language.postfixOps

object NpmPlugin extends AutoPlugin {

  object autoImport {
    lazy val npmInstall = taskKey[Unit]("Executes `npm install ` if a package.json file is present.")
  }

  override def trigger: PluginTrigger = allRequirements

  override def requires = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    Settings.npmInstall,
    (stage in Universal) <<= (stage in Universal) dependsOn autoImport.npmInstall
  )

  object Settings {
    lazy val npmInstall = autoImport.npmInstall := command(Seq("--prefix ./client", "install"), thisProject.value.base, streams.value)
  }

  def command(args: Seq[String], cwd: File, steamTask: TaskStreams): Unit = {
    val pkgFileExists = (cwd / "client" / "package.json").exists

    if (pkgFileExists) {
      val cmd = "npm"

      val fullCmd = (Seq[String](cmd) ++ args)
        .filter(_.length > 0)
        .mkString(" ")

      Some(steamTask) foreach { _.log.info(s"$cwd: npm ${args.mkString(" ")}") }

      println(Process(fullCmd, cwd).!!)
    }
  }
}