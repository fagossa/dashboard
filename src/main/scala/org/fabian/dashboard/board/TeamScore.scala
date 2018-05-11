package org.fabian.dashboard.board

import java.time.LocalDateTime
import play.api.libs.json.{Format, JsResult, JsValue, Json}
import scala.collection.immutable.Queue

case class Measure(time: LocalDateTime, value: BigDecimal)

object Measure {

  implicit val localDateFormat = new Format[LocalDateTime] {
    override def reads(json: JsValue): JsResult[LocalDateTime] =
      json.validate[String].map(LocalDateTime.parse)

    override def writes(o: LocalDateTime): JsValue =
      Json.toJson(o.toString)
  }

  implicit val measureFormat: Format[Measure] = Json.format[Measure]

  import scala.util.Random
  def buildRandomMeasure(max: Int)(implicit generator: Random) = {
    import DataTools._
    Measure(LocalDateTime.now(), randomValueUntil(max))
  }

}

case class TeamScore(
  name: String,
  background: String = "black",
  billed: Queue[Measure] = Queue.empty[Measure]
) {

  def addMeasures(newMeasures: Measure*): TeamScore = {
    val updatedMeasures = billed ++ newMeasures
    copy(billed = updatedMeasures.drop(updatedMeasures.size - 10))
  }

}

object TeamScore {
  implicit val format: Format[TeamScore] = Json.format[TeamScore]
}

case class Label(name: String) extends AnyVal

object Label {
  implicit val labelFormat: Format[Label] = Json.format[Label]
}

case class BoardResult(
  labels: List[Label],
  scores: List[TeamScore]
)

object BoardResult {
  implicit val boardFormat: Format[BoardResult] = Json.format
}

case class MeasurePayload(user: String, billed: Int) {
  def buildMeasures: Measure = Measure(LocalDateTime.now(), BigDecimal(billed))
}

object MeasurePayload {
  implicit val boardFormat: Format[MeasurePayload] = Json.format
}

object DataTools {

  import scala.util.Random
  def randomValueUntil(limit: Int)(implicit generator: Random): BigDecimal =
    BigDecimal(math.abs(generator.nextInt(limit)))

  def randomColor(implicit generator: Random): String = {
    val nextInt: Int = generator.nextInt(256 * 256 * 256)
    s"#${nextInt.toHexString.toUpperCase}"
  }

}