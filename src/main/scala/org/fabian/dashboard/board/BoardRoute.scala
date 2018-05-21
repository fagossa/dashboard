package org.fabian.dashboard.board

import scala.concurrent.duration._

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl._
import org.slf4j.LoggerFactory

class BoardRoute(service: BoardService)(implicit system: ActorSystem) {
  val logger = LoggerFactory.getLogger(getClass)

  implicit val ec = system.dispatchers.lookup("my-dispatcher")

  implicit val jsonStreamingSupport = EntityStreamingSupport.json()

  def routes = path("events") { streamEvents ~ updateMeasures}

  private def streamEvents = {
    import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._
    import akka.http.scaladsl.model.sse.ServerSentEvent
    get(
      complete {
        Source
          .tick(2.seconds, 2.seconds, NotUsed)
          .mapAsync(1)(_ => service.boardResults.map(result => ServerSentEvent(result.stringify)))
          .keepAlive(1.second, () => ServerSentEvent.heartbeat)
      })
  }

  private def updateMeasures = {
    import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._
    post {
      entity(as[MeasurePayload]) { payload =>
        onSuccess(service.updateScore(payload)) {
          case Some(updatedPayload) => complete(updatedPayload)
          case None => complete(StatusCodes.BadRequest, "Impossible to update measure")
        }
      }
    }
  }
}
