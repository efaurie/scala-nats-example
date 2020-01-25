package example.model

import java.time.Instant

import play.api.libs.json.{Json, Reads, Writes}

object StringMessageJson {
  implicit val stringMessageWrites: Writes[StringMessage] = Json.writes[StringMessage]
  implicit val stringMessageReads: Reads[StringMessage] = Json.reads[StringMessage]
}

case class StringMessage(eventTs: Instant, seqNum: Long, message: String) {
  override def toString: String = {
    s"[${eventTs.toString}][${this.getClass.getSimpleName}] $message"
  }
}
