package routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream._
import akka.stream.scaladsl._
import akka.NotUsed
import akka.actor.ActorSystem

import scala.concurrent.duration._
import akka.http.scaladsl.marshalling.{Marshaller, ToResponseMarshaller}
import akka.http.scaladsl.model.HttpEntity.ChunkStreamPart
import models.Character
import repositories.ShakespeareRepository
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import play.api.libs.json.Json

class ShakespeareRoute(workingDirectory: String, repository: ShakespeareRepository)(implicit
  system: ActorSystem,
  materializer: ActorMaterializer
) {

  val source: Source[Character, NotUsed] = Source(repository.romeoEtJuliette)
  val romeoEtJulietteIterable = repository.romeoEtJuliette.toIterator

  def toNewLineFlow[A]: Flow[A, String, NotUsed] = Flow[A].map(_.toString + "\n")

  implicit val toResponseMarshaller: ToResponseMarshaller[Source[String, Any]] =
    Marshaller.opaque { items =>
      val data = items.map(item => ChunkStreamPart(item))
      HttpResponse(entity = HttpEntity.Chunked(MediaTypes.`application/json`, data))
    }

  def throttler[A](duration: FiniteDuration): Flow[A, A, NotUsed] =
    Flow[A].throttle(1, duration, 1, ThrottleMode.shaping)

  def routes = path("romeoEtJuliette") {
    handleWebSocketMessages(Flow[Message].mapConcat {
      case tm: TextMessage if romeoEtJulietteIterable.hasNext =>
        println("ShakespeareRoute::socket - 1")
        val character: Character = romeoEtJulietteIterable.next()
        TextMessage(Json.stringify(Json.toJson(character))) :: Nil

      case other =>
        println("ShakespeareRoute::socket - 2")
        println(other)
        Nil
    })
  } ~
    path("romeoAndJuliette") { get(complete(romeoAndJulietteSource)) } ~
    pathEnd { getFromFile(s"$workingDirectory/theatre.html") }

  private def romeoAndJulietteSource: Source[String, NotUsed] = source
    .map(c => s"${c.name} - ${c.text}")
    .via(toNewLineFlow)
    .via(throttler(0.5.second))

}
