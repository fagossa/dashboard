package org.fabian.dashboard.common

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directives.{ get, _ }
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import play.api.libs.json.{ JsString, JsValue, Json, Writes }

class StatusRoute() {

  val logger = LoggerFactory.getLogger(getClass)

  val isoDateTimeWrites = new Writes[LocalDateTime] {
    def writes(d: LocalDateTime): JsValue = JsString(d.format(DateTimeFormatter.ISO_DATE_TIME))
  }

  def routes: Route = buildInfo

  private val buildInfo = {
    path("build") {
      get {
        complete(
          HttpResponse(
            entity = HttpEntity(ContentTypes.`application/json`, Json.stringify(buildInfoToJson))
          )
        )
      }
    }
  }

  private def buildInfoToJson: JsValue = {
    import org.fabian.build.BuildInfo._
    Json.obj(
      "version"   -> version,
      "timestamp" -> Json.toJson(LocalDateTime.now())(isoDateTimeWrites)
    )
  }

}
