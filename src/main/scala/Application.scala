import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory

import org.fabian.dashboard.board.BoardService
import org.fabian.dashboard.repositories.ItemRepository
import org.fabian.dashboard.routes.JsonRoutes
import org.fabian.dashboard.routes.IndexRoutes
import org.fabian.dashboard.routes.AssetsRoute
import org.fabian.dashboard.routes.StreamingRoute
import org.fabian.dashboard.routes.BoardRoute

import scala.util.{Failure, Success}

object Application extends App {
  implicit val system = ActorSystem("go-as-function-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val logger = LoggerFactory.getLogger(getClass)

  val port: Int = 8080
  val interface = "0.0.0.0"
  val shutdownTimeout = 60.seconds

  val workingDirectory = System.getProperty("user.dir")

  val routes =
    pathPrefix("json") { new JsonRoutes(new ItemRepository()).routes } ~
    pathPrefix("assets") { new AssetsRoute(workingDirectory).routes } ~
    pathPrefix("streaming") { new StreamingRoute().routes } ~
    pathPrefix("board") { new BoardRoute(new BoardService()).routes } ~
    new IndexRoutes(workingDirectory).routes

  logger.info(s"Starting server on ${interface}:${port}")
  Http().bindAndHandle(routes, interface, port)
    .onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        logger.info(s"Server is listening on ${address.getHostString}:${address.getPort}")
        registerShutdownHook(binding)
      case Failure(ex) =>
        logger.error(s"Server could not be started", ex)
        stopAll()
    }

  private def registerShutdownHook(binding: Http.ServerBinding): Unit = {
    scala.sys.addShutdownHook {
      binding.unbind().onComplete { _ =>
        stopAll()
      }
    }
    ()
  }

  private def stopAll(): Unit = {
    system.terminate()
    Await.result(system.whenTerminated, shutdownTimeout)
  }

}
