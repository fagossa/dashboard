import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import board.{BoardRoutes, BoardService}
import repositories.{ItemRepository, ShakespeareRepository}

import scala.io.StdIn
import routes.HelloRoute
import routes.JsonRoutes
import routes.IndexRoutes
import routes.AssetsRoute
import routes.StreamingRoute
import routes.ShakespeareRoute

object Application extends App {
  implicit val system = ActorSystem("go-as-function-system")
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher

  val port: Int = 8080

  val workingDirectory = System.getProperty("user.dir")

  val route =
    path("hello") { new HelloRoute().routes } ~
    pathPrefix("json") { new JsonRoutes(new ItemRepository()).routes } ~
    pathPrefix("assets") { new AssetsRoute(workingDirectory).routes } ~
    pathPrefix("streaming") { new StreamingRoute().routes } ~
    pathPrefix("theatre") { new ShakespeareRoute(workingDirectory, new ShakespeareRepository()).routes } ~
    new IndexRoutes(workingDirectory).routes ~
    pathPrefix("board") { new BoardRoutes(new BoardService()).routes }

  val bindingFuture = Http().bindAndHandle(route, "localhost", port)

  println(s"Server online at http://localhost:$port/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
