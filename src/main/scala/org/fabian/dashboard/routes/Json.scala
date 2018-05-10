package org.fabian.dashboard.routes

import scala.concurrent.Future

import akka.http.scaladsl.model.StatusCodes
import akka.http.javadsl.common.EntityStreamingSupport
import akka.http.scaladsl.server.Directives._

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

import org.fabian.dashboard.models.Item
import org.fabian.dashboard.repositories.ItemRepository

import scala.concurrent.ExecutionContext

class JsonRoutes(itemRepository: ItemRepository)(implicit ec: ExecutionContext) {

  //implicit val itemFormat = jsonFormat2(org.fabian.dashboard.models.Item)
  implicit val jsonStreamingSupport = EntityStreamingSupport.json()

  def routes = get {
    path(LongNumber) { id =>
      val maybeItem: Future[Option[Item]] = itemRepository.fetchItem(id)

      onSuccess(maybeItem) {
        case Some(item) => complete(item)
        case None => complete(StatusCodes.NotFound)
      }
    }
  }

}
