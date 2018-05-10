package org.fabian.dashboard.board

import java.time.LocalDateTime

import play.api.libs.json.{Format, JsResult, JsValue, Json}

case class Measure(time: LocalDateTime, value: BigDecimal)

object Measure {

  implicit val localDateFormat = new Format[LocalDateTime] {
    override def reads(json: JsValue): JsResult[LocalDateTime] =
      json.validate[String].map(LocalDateTime.parse)

    override def writes(o: LocalDateTime): JsValue =
      Json.toJson(o.toString)
  }

  implicit val measureFormat: Format[Measure] = Json.format[Measure]
}

case class TeamScore(
  name: String,
  background: String,
  measures: List[Measure],
  anotherPoints: List[BigDecimal]
)

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
  implicit val boardFormat: Format[BoardResult] = Json.format[BoardResult]
}
