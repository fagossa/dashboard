package models

import play.api.libs.json.{Format, Json}

case class Character(name: String, text: String)

object Character {
  implicit val format: Format[Character] = Json.format[Character]

}
