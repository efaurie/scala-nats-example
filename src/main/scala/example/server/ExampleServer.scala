package example.server

import java.time.Instant

import example.model.AckJson._
import example.model.StringMessageJson._
import example.model.serdes.JsonSerde
import example.model.{Ack, StringMessage}
import example.nats.{Nats, NatsJsonEndpoint}

object ExampleServer {
  def apply(nats: Nats): ExampleServer = {
    new ExampleServer(nats)
  }
}

class ExampleServer(nats: Nats) {
  // Create Serde's for Request / Response Types
  implicit val msgSerde: JsonSerde[StringMessage] = JsonSerde[StringMessage]()
  implicit val ackSerde: JsonSerde[Ack] = JsonSerde[Ack]()

  // Register Endpoint Behaviors
  nats.asyncSubscribe("ExampleServer.ack", NatsJsonEndpoint(nats, ack))
  nats.asyncSubscribe("ExampleServer.reply", NatsJsonEndpoint(nats, reply))

  def ack(msg: StringMessage): Ack = {
    Ack(Instant.now, isFailure = false)
  }

  var replySeqNum: Long = 1

  def reply(msg: StringMessage): StringMessage = {
    val response = StringMessage(Instant.now, replySeqNum, s"Reply $replySeqNum")
    replySeqNum += 1
    response
  }
}
