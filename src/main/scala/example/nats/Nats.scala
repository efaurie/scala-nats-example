package example.nats

import io.nats.client.{Connection, Dispatcher, MessageHandler, Nats => JNats}
import play.api.libs.json.Writes

import scala.concurrent.duration._

object Nats {
  def apply(hostname: String = "localhost", port: Int = 4222): Nats = {
    new Nats(hostname, port)
  }
}

class Nats(hostname: String, port: Int) {
  import example.ext._

  val url: String = s"nats://$hostname:$port"
  private val conn: Connection = JNats.connect(url)

  def publish[T](subject: String, msg: T)(implicit codec: Writes[T]): Unit = {
    conn.publish(subject, codec.writes(msg).toBytes)
  }

  def asyncSubscribe(subject: String, handler: MessageHandler): Dispatcher = {
    val dispatcher = conn.createDispatcher(handler)
    dispatcher.subscribe(subject)
    dispatcher
  }

  def drainAndClose(timeout: Duration): Unit = try {
    conn.drain(timeout)
  } finally {
    conn.close()
  }
}
