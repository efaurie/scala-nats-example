package example.nats

final case class NatsJsonDecodeException(private val message: String = "", private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
