package example.model

import java.time.Instant

case class Ack(eventTs: Instant, isFailure: Boolean) {
  override def toString: String = {
    s"[${eventTs.toString}][${this.getClass.getSimpleName}] ${if (isFailure) "FAILED" else "SUCCESS"}"
  }
}
