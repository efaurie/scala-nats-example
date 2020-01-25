package example.model

import play.api.libs.json.{Json, Reads, Writes}

package object serdes {
  implicit val stringMessageWrites: Writes[StringMessage] = Json.writes[StringMessage]
  implicit val stringMessageReads: Reads[StringMessage] = Json.reads[StringMessage]
}
