package example

import java.time.Instant

import example.model.StringMessage
import example.nats.{Nats, NatsJsonHandler}

import scala.concurrent.duration._

object BasicExample extends App {
  import example.model.serdes._
  implicit val serde: JsonSerde[StringMessage] = JsonSerde[StringMessage]()

  val subject: String = "scala.nats.example"

  val nats: Nats = Nats("localhost", 4222)

  // Subscribe and convert -- Bytes -> UTF8 -> JSON -> StringMessage
  nats.asyncSubscribe(subject, NatsJsonHandler[StringMessage](msg => println(msg)))

  // Publish 10 StringMessage's, this will automatically convert StringMessage -> JSON -> UTF8 -> Bytes
  (1 to 10).foreach { seqNum =>
    nats.publish(subject, StringMessage(Instant.now, seqNum, s"This is example $seqNum!"))
  }

  // Give it three seconds to drain all messages and then terminate
  nats.drainAndClose(3 seconds)
}
