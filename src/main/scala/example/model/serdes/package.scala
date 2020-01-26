package example.model

import play.api.libs.json.{Json, Reads, Writes}

package object serdes {
  implicit val ackWrites: Writes[Ack] = Json.writes[Ack]
  implicit val ackReads: Reads[Ack] = Json.reads[Ack]
  implicit val stringMessageWrites: Writes[StringMessage] = Json.writes[StringMessage]
  implicit val stringMessageReads: Reads[StringMessage] = Json.reads[StringMessage]
}
