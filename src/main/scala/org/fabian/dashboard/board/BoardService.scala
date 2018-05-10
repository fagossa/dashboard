package org.fabian.dashboard.board

import java.time.LocalDateTime

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

class BoardService(implicit ec: ExecutionContext) {
  implicit val r = scala.util.Random
  val maxResults = 10

  def boardResults: Future[BoardResult] = {
    Future(
      BoardResult(
        labels = (1 to maxResults).map(i => Label(i.toString)).toList.reverse,
        scores = List(
          randomScoreForTeam("team 1", "#f87979"),
          randomScoreForTeam("team 2", "#f879ee"),
          randomScoreForTeam("team 3", "#00ee79")
        )
      )
    )
  }

  private def randomScoreForTeam(name: String, background: String): TeamScore = {
    val lastTenValues: List[Measure] =
      (1 to maxResults).map(_ => Measure(LocalDateTime.now(), randomValueUntil(100))).toList
    TeamScore(
      name,
      background = background,
      measures = lastTenValues,
      anotherPoints = List(randomValueUntil(maxResults))
    )
  }

  private def randomValueUntil(limit: Int)(implicit generator: Random): BigDecimal =
    BigDecimal(math.abs(generator.nextInt(limit)))

}
