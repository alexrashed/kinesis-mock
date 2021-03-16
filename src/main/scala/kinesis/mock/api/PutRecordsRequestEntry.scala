package kinesis.mock.api

import io.circe._

final case class PutRecordsRequestEntry(
    data: Array[Byte],
    explicitHashKey: Option[String],
    partitionKey: String
)

object PutRecordsRequestEntry {
  implicit val putRecordsRequestEntryCirceEncoder
      : Encoder[PutRecordsRequestEntry] =
    Encoder.forProduct3(
      "Data",
      "ExplicitHashKey",
      "PartitionKey"
    )(x => (x.data, x.explicitHashKey, x.partitionKey))

  implicit val putRecordsRequestEntryCirceDecoder
      : Decoder[PutRecordsRequestEntry] =
    x =>
      for {
        data <- x.downField("Data").as[Array[Byte]]
        explicitHashKey <- x.downField("ExplicitHashKey").as[Option[String]]
        partitionKey <- x.downField("PartitionKey").as[String]
      } yield PutRecordsRequestEntry(
        data,
        explicitHashKey,
        partitionKey
      )
}