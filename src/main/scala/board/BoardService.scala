package board

import scala.concurrent.Future

class BoardService {

  def boardResults: Future[List[Team]] = {
    Future.successful(List(
      Team("team1", BigDecimal(100)),
      Team("team2", BigDecimal(1000)),
      Team("team3", BigDecimal(10))
    ))
  }

}
