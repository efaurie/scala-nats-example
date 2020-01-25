package example.nats

import example.model.serdes.JsonSerde
import io.nats.client.{Message, MessageHandler}

object NatsJsonEndpoint {
  def apply[A, B](nats: Nats, func: A => B)(implicit rx: JsonSerde[A], tx: JsonSerde[B]): NatsJsonEndpoint[A, B] = {
    new NatsJsonEndpoint[A, B](nats, func)
  }
}

class NatsJsonEndpoint[A, B](nats: Nats, func: A => B)(implicit rx: JsonSerde[A], tx: JsonSerde[B]) extends MessageHandler {
  import example.ext._

  override def onMessage(msg: Message): Unit = {
    val in = rx.unmarshal(msg.getData)
      .getOrElse { throw NatsJsonDecodeException(s"Failed to decode message: ${msg.toHex}") }

    nats.publish(msg.getReplyTo, func(in))
  }
}
