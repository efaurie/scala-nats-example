package example.nats

import example.model.serdes.Serde
import io.nats.client.{Connection, Dispatcher, MessageHandler, Nats => JNats}

import scala.compat.java8.FutureConverters._
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Nats {
  def apply(hostname: String = "localhost", port: Int = 4222): Nats = {
    new Nats(hostname, port)
  }
}

class Nats(hostname: String, port: Int) {
  import example.ext._

  val url: String = s"nats://$hostname:$port"
  private val conn: Connection = JNats.connect(url)

  def publish[A](subject: String, msg: A)(implicit serde: Serde[A]): Unit = {
    conn.publish(subject, serde.marshal(msg))
  }

  def asyncSubscribe(subject: String, handler: MessageHandler): Dispatcher = {
    val dispatcher = conn.createDispatcher(handler)
    dispatcher.subscribe(subject)
    dispatcher
  }

  def request[A, B](
    subject: String, msg: A
  )(implicit ec: ExecutionContext, txSerde: Serde[A], rxSerde: Serde[B]): Future[Try[B]] = {
    conn.request(subject, txSerde.marshal(msg))
      .toScala
      .map { msg => rxSerde.unmarshal(msg.getData) }
  }

  def drainAndClose(timeout: Duration): Unit = try {
    conn.drain(timeout)
  } finally {
    conn.close()
  }
}
