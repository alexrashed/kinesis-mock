package kinesis.mock.models

import cats.Eq
import io.circe._

final case class ConsumerName(consumerName: String) {
  override def toString(): String = consumerName
}

object ConsumerName {
  implicit val consumerNameCirceEncoder: Encoder[ConsumerName] =
    Encoder[String].contramap(_.consumerName)
  implicit val consumerNameCirceDecoder: Decoder[ConsumerName] =
    Decoder[String].map(ConsumerName.apply)
  implicit val consumerNameEq: Eq[ConsumerName] = Eq.fromUniversalEquals
}