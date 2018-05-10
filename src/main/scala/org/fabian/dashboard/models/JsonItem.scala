package org.fabian.dashboard.models

import play.api.libs.json.{Format, Json}

case class Item(name: String, id: Long)

object Item {
  implicit val format: Format[Item] = Json.format[Item]
}
