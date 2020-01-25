package example

import java.nio.charset.StandardCharsets

import io.nats.client.Message
import play.api.libs.json.{JsValue, Json}
import scala.concurrent.duration._
import java.time.{Duration => JDuration}

package object ext {
  implicit def asJavaDuration(d: Duration): JDuration = JDuration.ofNanos(d.toNanos)

  implicit class RichJsValue(val in: JsValue) extends AnyVal {
    def toBytes: Array[Byte] = in.toString.getBytes(StandardCharsets.UTF_8)
  }

  implicit class RichMessage(val msg: Message) extends AnyVal {
    def asJson: JsValue = Json.parse(new String(msg.getData, StandardCharsets.UTF_8))
    def toHex: String = msg.getData.map("%02x" format _).mkString
  }
}
