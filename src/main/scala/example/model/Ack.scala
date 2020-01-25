package example.model

import java.time.Instant

import play.api.libs.json.{Json, Reads, Writes}

object AckJson {
  implicit val ackWrites: Writes[Ack] = Json.writes[Ack]
  implicit val ackReads: Reads[Ack] = Json.reads[Ack]
}

case class Ack(eventTs: Instant, isFailure: Boolean) {
  override def toString: String = {
    s"[${eventTs.toString}][${this.getClass.getSimpleName}] ${if (isFailure) "FAILED" else "SUCCESS"}"
  }
}
