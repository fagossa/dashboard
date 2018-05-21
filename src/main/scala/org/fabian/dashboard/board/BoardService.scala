package org.fabian.dashboard.board

import scala.concurrent.Future
import akka.actor.ActorRef
import akka.util.Timeout
import org.slf4j.LoggerFactory

class BoardService(actorRepository: ActorRef)(implicit timeout: Timeout) {

  val logger = LoggerFactory.getLogger(getClass)

  import akka.pattern.ask

  def boardResults: Future[BoardResult] =
    (actorRepository ? BoardActorRepository.Messages.GetState).mapTo[BoardResult]

  def updateScore(payload: MeasurePayload): Future[Option[TeamScore]] = {
    logger.info(s"Updating score for <${payload.user}>")
    (actorRepository ? BoardActorRepository.Messages.UpdateScore(payload)).mapTo[Option[TeamScore]]
  }

}
