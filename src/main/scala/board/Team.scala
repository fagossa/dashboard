package board

import play.api.libs.json.{Format, Json}

case class Team(name: String, points: BigDecimal)

object Team {
  implicit val format: Format[Team] = Json.format[Team]
}
