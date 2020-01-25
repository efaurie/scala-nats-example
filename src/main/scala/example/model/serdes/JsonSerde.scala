package example.model.serdes

import java.nio.charset.{Charset, StandardCharsets}

import play.api.libs.json.{Json, Reads, Writes}

import scala.util.Try

object JsonSerde {
  def apply[A]()(implicit w: Writes[A], r: Reads[A]): JsonSerde[A] = new JsonSerde[A]()
}

class JsonSerde[A](implicit w: Writes[A], r: Reads[A]) extends Serde[A] {
  val charset: Charset = StandardCharsets.UTF_8

  override def marshal(in: A): Array[Byte] = {
    w.writes(in).toString.getBytes(charset)
  }

  override def unmarshal(in: Array[Byte]): Try[A] = Try {
    val msg = new String(in, charset)
    val jsonMsg = Json.parse(msg)

    r.reads(jsonMsg).get
  }
}
