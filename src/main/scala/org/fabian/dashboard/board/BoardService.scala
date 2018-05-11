package org.fabian.dashboard.board

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

import org.fabian.dashboard.board.DataTools.randomColor

class BoardService(implicit ec: ExecutionContext) {

  implicit val r = scala.util.Random
  val maxResults = 10

  val state: mutable.Map[String, TeamScore] = mutable.Map.empty

  def boardResults: Future[BoardResult] = {
    Future(
      BoardResult(
        labels = extractLabels,
        scores = state.values.toList
      )
    )
  }

  def updateScore(payload: MeasurePayload): Future[String] = {
    val name = payload.user
    state.get(name) match {
      case Some(team) =>
        val updatedScore = team.addMeasures(payload.buildMeasures)
        this.state.put(name, updatedScore)
        Future.successful(name)

      case None =>
        val score = TeamScore(name, background = randomColor).addMeasures(payload.buildMeasures)
        this.state += (name -> score)
        Future.successful(name)
    }
  }

  private def extractLabels =
    (1 to maxResults).map(i => Label(i.toString)).toList

}
