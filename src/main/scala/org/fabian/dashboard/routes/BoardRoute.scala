package org.fabian.dashboard.routes

import scala.concurrent.duration._

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.stream.scaladsl._
import org.slf4j.LoggerFactory

import org.fabian.dashboard.board.BoardService

class BoardRoute(service: BoardService)
(implicit system: ActorSystem, materializer: ActorMaterializer) {
  val logger = LoggerFactory.getLogger(getClass)

  implicit val ec = system.dispatchers.lookup("my-dispatcher")
  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

  def routes = path("events") {
    get(
      complete {
        Source
          .tick(2.seconds, 2.seconds, NotUsed)
          .mapAsync(1)(_ => service.boardResults.map(result => ServerSentEvent(result.stringify)))
          .keepAlive(1.second, () => ServerSentEvent.heartbeat)
      })
  }

}
