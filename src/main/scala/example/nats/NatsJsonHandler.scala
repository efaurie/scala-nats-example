package example.nats

import io.nats.client.{Message, MessageHandler}
import play.api.libs.json.Reads

final case class NatsJsonDecodeException(private val message: String = "", private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

object NatsJsonHandler {
  def apply[T](func: T => Unit)(implicit reader: Reads[T]): NatsJsonHandler[T] = {
    new NatsJsonHandler[T](func)(reader)
  }
}

class NatsJsonHandler[T](func: T => Unit)(implicit reader: Reads[T]) extends MessageHandler {
  import example.ext._

  override def onMessage(msg: Message): Unit = {
    val parsedMsg: T = reader
      .reads(msg.asJson)
      .getOrElse { throw NatsJsonDecodeException(s"Failed to decode message: ${msg.toHex}") }

    func(parsedMsg)
  }
}
