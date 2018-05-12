package org.fabian.dashboard.board

import java.time.LocalDateTime

import akka.actor.ActorRef

import scala.collection.immutable.Queue
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import play.api.libs.json.Json
import org.fabian.dashboard.routes.BoardRoute

class TeamScoreSpec extends WordSpec with MustMatchers with ScalaFutures with ScalatestRouteTest {

  implicit val ec = scala.concurrent.ExecutionContext.global

  import scala.concurrent.duration._
  implicit val actorTimeout: Timeout = Timeout(1.seconds)

  "a TeamScore" must {

    "accept less than 10 elements" in {
      // given
      val measures = (1 to 3).map(i => Measure(LocalDateTime.now(), BigDecimal(i)))

      // when
      val result = TeamScore("a team", billed = Queue.empty[Measure]).addMeasures(measures:_*)

      // then
      result.billed must have size 3
      result.billed.head.value must be(BigDecimal(1))
      result.billed.map(_.value) must be(Queue(BigDecimal(1), BigDecimal(2), BigDecimal(3)))
    }

    "allow only 10 bills" in {
      // given
      val measures = (1 to 15).map(i => Measure(LocalDateTime.now(), BigDecimal(i)))

      // when
      val result = TeamScore("a team", billed = Queue.empty[Measure]).addMeasures(measures:_*)

      // then
      result.billed must have size 10
    }

    "discard first elements" in {
      // given
      val measures = (1 to 10).map(i => Measure(LocalDateTime.now(), BigDecimal(i)))
      val result = TeamScore("a team", billed = Queue.empty[Measure])
        .addMeasures(measures:_*)
        .addMeasures(Measure(LocalDateTime.now(), BigDecimal(11)))

      // then
      result.billed must have size 10
      info(result.billed.map(_.value).mkString(","))
      result.billed.head.value must be(BigDecimal(2))
    }

  }

  "a BoardService" must {

    "parse MeasurePayload" in {
      val payload = """
        |{
        |  "user": "toto",
        |  "billed": 10
        |}
      """.stripMargin
      val result = Json.parse(payload).asOpt[MeasurePayload]
      result mustBe 'defined
      result.get.buildMeasures.value must be(BigDecimal(10))
    }

    "create score when do not exists" in {
      val repository: ActorRef = system.actorOf(BoardActorRepository.props)
      val service = new BoardService(repository)
      // When
      service.updateScore(MeasurePayload("toto", 10)).futureValue
      val result: BoardResult = service.boardResults.futureValue

      // Then
      result.scores must have size 1
      result.scores.head.billed must have size 1
      result.scores.head.billed.head.value must be(BigDecimal(10))
    }

    "accumulate scores when they not exists" in {
      val repository: ActorRef = system.actorOf(BoardActorRepository.props)
      val service = new BoardService(repository)

      // When
      service.updateScore(MeasurePayload("titi", 10)).futureValue
      service.updateScore(MeasurePayload("titi", 15)).futureValue
      service.updateScore(MeasurePayload("titi", 20)).futureValue

      val result: BoardResult = service.boardResults.futureValue

      // Then
      result.scores must have size 1
      result.scores.head.billed must have size 3
      result.scores.head.billed.map(_.value) must be(List(BigDecimal(10), BigDecimal(15), BigDecimal(20)))
    }

    "handle different users" in {
      val repository: ActorRef = system.actorOf(BoardActorRepository.props)
      val service = new BoardService(repository)

      // When
      service.updateScore(MeasurePayload("titi", 10)).futureValue
      service.updateScore(MeasurePayload("tata", 15)).futureValue
      service.updateScore(MeasurePayload("tutu", 20)).futureValue

      val result: BoardResult = service.boardResults.futureValue

      // Then
      result.scores must have size 3
      info(result.scores.map(_.name).mkString(" "))
      result.scores.map(_.billed.size) must be (List(1, 1, 1))
      result.scores.flatMap(_.billed).map(_.value).toSet must be(Set(BigDecimal(10), BigDecimal(15), BigDecimal(20)))
    }

  }

  "a BoardRoute" must {

    "accept post messages" in {
      import akka.http.scaladsl.model._
      import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport._

      val repository: ActorRef = system.actorOf(BoardActorRepository.props)

      //when
      val routes = new BoardRoute(new BoardService(repository)).routes
      val payload = MeasurePayload("toto", 35)

      Post("/events", payload) ~> routes ~> check {
        //then
        status mustBe StatusCodes.OK
        contentType mustBe ContentTypes.`application/json`

        val resultScore = entityAs[TeamScore]
        resultScore.name must be("toto")
        resultScore.billed.map(_.value) must be(Queue(BigDecimal(35)))
      }
    }

  }

}