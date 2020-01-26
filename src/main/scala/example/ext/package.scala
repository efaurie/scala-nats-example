package example

import java.nio.charset.StandardCharsets

import io.nats.client.Message
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.duration._
import java.time.{Duration => JDuration}
import java.util.Locale

package object ext {
  implicit def asJavaDuration(d: Duration): JDuration = JDuration.ofNanos(d.toNanos)

  implicit class RichJsValue(val in: JsValue) extends AnyVal {
    def toBytes: Array[Byte] = in.toString.getBytes(StandardCharsets.UTF_8)
  }

  implicit class RichMessage(val msg: Message) extends AnyVal {
    def asJson: JsValue = Json.parse(new String(msg.getData, StandardCharsets.UTF_8))
    def toHex: String = msg.getData.map("%02x" format _).mkString
  }

  def timeit(fn: => Unit): Unit = {
    val start = System.nanoTime
    fn
    val stop = System.nanoTime
    println(s"Time Elapsed: ${(stop - start).nanos.pretty()}")
  }

  implicit class PrettyPrintableDuration(val duration: Duration) extends AnyVal {
    /** Selects most appropriate TimeUnit for given duration and formats it accordingly */
    def pretty(precision: Int = 4): String = {
      require(precision > 0, "precision must be > 0")

      duration match {
        case d: FiniteDuration =>
          val nanos = d.toNanos
          val unit = chooseUnit(nanos)
          val value = nanos.toDouble / NANOSECONDS.convert(1, unit)

          s"%.${precision}g %s%s".formatLocal(
            Locale.ROOT,
            value,
            abbreviate(unit),
            s" ($nanos ns)")

        case Duration.MinusInf => s"-∞ (minus infinity)"
        case Duration.Inf      => s"∞ (infinity)"
        case _                 => "undefined"
      }
    }

    def chooseUnit(nanos: Long): TimeUnit = {
      val d = nanos.nanos

      if (d.toDays > 0) DAYS
      else if (d.toHours > 0) HOURS
      else if (d.toMinutes > 0) MINUTES
      else if (d.toSeconds > 0) SECONDS
      else if (d.toMillis > 0) MILLISECONDS
      else if (d.toMicros > 0) MICROSECONDS
      else NANOSECONDS
    }

    def abbreviate(unit: TimeUnit): String = unit match {
      case NANOSECONDS  => "ns"
      case MICROSECONDS => "μs"
      case MILLISECONDS => "ms"
      case SECONDS      => "s"
      case MINUTES      => "min"
      case HOURS        => "h"
      case DAYS         => "d"
    }
  }
}
