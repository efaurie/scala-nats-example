package example.model.serdes

import scala.util.Try

trait Serde[A] {
  def marshal(in: A): Array[Byte]
  def unmarshal(in: Array[Byte]): Try[A]
}
