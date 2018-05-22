package org.fabian.dashboard.board

import akka.actor.{ Actor, ActorLogging, Props }
import akka.event.Logging
import org.fabian.dashboard.board.BoardActorRepository.Messages.{ GetState, UpdateScore }
import org.fabian.dashboard.board.DataTools.randomColor

import scala.collection.mutable

class BoardActorRepository extends Actor with ActorLogging {

  override val log = Logging(context.system, this)

  implicit val r = scala.util.Random

  val maxResults = 10

  val state: mutable.Map[String, TeamScore] = mutable.Map.empty

  def receive = {
    case GetState =>
      sender ! BoardResult(labels = extractLabels, scores = state.values.toList)

    case UpdateScore(payload) =>
      log.info(s"======= Keys: <${state.keys.mkString(",")}> ========")
      val name = payload.user
      val result: TeamScore = state.get(name) match {
        case Some(team) =>
          log.info(s"We have already seen group called <$name>")
          team.addMeasures(payload.buildMeasures)

        case None =>
          log.info(s"This is the first time seen group <$name>")
          TeamScore(name, background = randomColor).addMeasures(payload.buildMeasures)
      }
      state += (name -> result)
      sender ! Some(result)

    case _ =>
      log.warning("received unknown message")
  }

  private def extractLabels =
    (1 to maxResults).map(i => Label(i.toString)).toList

  override def preStart(): Unit = {
    super.preStart()
    log.info("Starting board repository")
  }

  override def postStop(): Unit = {
    super.postStop()
    log.info("Stopping board repository")
  }
}

object BoardActorRepository {

  object Messages {
    case class GetState()
    case class UpdateScore(payload: MeasurePayload)
  }

  def props = Props[BoardActorRepository]
}
