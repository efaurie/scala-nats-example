package example.nats

import example.model.serdes.JsonSerde
import io.nats.client.{Message, MessageHandler}


object NatsJsonHandler {
  def apply[A](func: A => Unit)(implicit rx: JsonSerde[A]): NatsJsonHandler[A] = {
    new NatsJsonHandler[A](func)
  }
}

class NatsJsonHandler[A](func: A => Unit)(implicit rx: JsonSerde[A]) extends MessageHandler {
  import example.ext._

  override def onMessage(msg: Message): Unit = {
    val parsedMsg: A = rx.unmarshal(msg.getData)
      .getOrElse { throw NatsJsonDecodeException(s"Failed to decode message: ${msg.toHex}") }

    func(parsedMsg)
  }
}
