package board

import akka.http.javadsl.common.EntityStreamingSupport
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

import scala.concurrent.ExecutionContext

class BoardRoutes(service: BoardService)(implicit ec: ExecutionContext) {
  implicit val jsonStreamingSupport = EntityStreamingSupport.json()

  lazy val routes = board

  private lazy val board = path("current") {
    get {
      onSuccess(service.boardResults) { results => complete(results) }
    }
  }

}
