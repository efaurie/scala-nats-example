package example.model

import java.time.Instant

case class StringMessage(eventTs: Instant, seqNum: Long, message: String) {
  override def toString: String = {
    s"[${eventTs.toString}][${this.getClass.getSimpleName}] $message"
  }
}
