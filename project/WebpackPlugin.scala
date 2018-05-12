import NpmPlugin.autoImport.npmInstall
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.{Universal, stage}
import sbt._

import scala.language.postfixOps

object WebpackPlugin extends AutoPlugin {

  object autoImport {
    val runGulp = taskKey[Unit]("Run webpack")
  }

  override def trigger: PluginTrigger = allRequirements

  override def requires = NpmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    Settings.runGulp,
    autoImport.runGulp <<= autoImport.runGulp dependsOn npmInstall,
    (stage in Universal) <<= (stage in Universal) dependsOn autoImport.runGulp
  )

  object Settings {
    lazy val runGulp = autoImport.runGulp := {
      val base = (sbt.Keys.baseDirectory in ThisBuild).value
      val cmd = "npm --prefix ./client run-script build"
      println(s"$cmd")
      println(Process(cmd, base) !!)
    }
  }

}