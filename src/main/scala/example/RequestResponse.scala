package example

import java.time.Instant

import example.model.serdes.JsonSerde
import example.model.{Ack, StringMessage}
import example.nats.Nats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object RequestResponse extends App {
  import example.ext._
  import example.model.AckJson._
  import example.model.StringMessageJson._
  implicit val msgSerde: JsonSerde[StringMessage] = JsonSerde[StringMessage]()
  implicit val ackSerde: JsonSerde[Ack] = JsonSerde[Ack]()

  val nats: Nats = Nats("localhost", 4222)
  val server: ExampleServer = ExampleServer(nats)

  def printReply[A](in: Try[Try[A]]): Unit = in match {
    case Success(response) => response match {  // The request succeeded
      case Success(ack) => println(ack)  // The parse of the response succeeded
      case Failure(e) => throw e
    }
    case Failure(e) => throw e
  }

  timeit {
    val ackFutures: Seq[Future[Any]] = (1 to 10).map { seqNum =>
      nats.request[StringMessage, Ack](
        "ExampleServer.ack", StringMessage(Instant.now, seqNum, s"This is example $seqNum!")
      ).transform { reply => printReply(reply); reply }
    }

    val replyFutures: Seq[Future[Any]] = (11 to 3000).map { seqNum =>
      nats.request[StringMessage, StringMessage](
        "ExampleServer.reply", StringMessage(Instant.now, seqNum, s"This is example $seqNum!")
      ).transform { reply => printReply(reply); reply }
    }

    Await.result(Future.sequence(replyFutures ++ ackFutures), Duration.Inf)
  }

  nats.drainAndClose(1 seconds)
}
