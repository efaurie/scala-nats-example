package example

import java.time.Instant

import example.model.serdes.JsonSerde
import example.model.{Ack, StringMessage}
import example.nats.Nats

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object RequestResponse extends App {
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

  // Publish 10 StringMessage's, this will automatically convert StringMessage -> JSON -> UTF8 -> Bytes
  (1 to 10).map { seqNum =>
    nats.request[StringMessage, Ack](
      "ExampleServer.ack", StringMessage(Instant.now, seqNum, s"This is example $seqNum!")
    ).onComplete { printReply }
  }

  (11 to 20).map { seqNum =>
    nats.request[StringMessage, StringMessage](
      "ExampleServer.reply", StringMessage(Instant.now, seqNum, s"This is example $seqNum!")
    ).onComplete { printReply }
  }

  // Give it three seconds to drain all messages and complete
  nats.drainAndClose(3 seconds)
}
