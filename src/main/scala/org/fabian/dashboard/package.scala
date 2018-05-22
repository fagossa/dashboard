package org.fabian

import play.api.libs.json.{ Json, Writes }

package object dashboard {

  implicit class JsonOps[T](t: T) {
    def stringify(implicit writes: Writes[T]) =
      Json.stringify(Json.toJson(writes.writes(t)))
  }

}
