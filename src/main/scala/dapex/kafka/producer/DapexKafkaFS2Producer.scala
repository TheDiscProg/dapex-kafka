package dapex.kafka.producer

import cats.effect.kernel.Async
import cats.syntax.all._
import dapex.kafka.KafkaTopic
import dapex.kafka.config.KafkaConfig
import dapex.messaging.DapexMessage
import fs2.kafka._
import org.typelevel.log4cats.Logger

class DapexKafkaFS2Producer[F[_]: Async: Logger](kafkaConfig: KafkaConfig)
    extends DapexKafkaProducer[F] {

  val producerSettings: ProducerSettings[F, String, String] =
    ProducerSettings[F, String, String]
      .withBootstrapServers(s"${kafkaConfig.bootstrapServer}:${kafkaConfig.port}")

  override def publishMessge(
      msg: DapexMessage,
      topic: KafkaTopic
  ): F[ProducerResult[String, String]] = {
    val str = DapexMessage.serializeToString(msg)
    val record: ProducerRecord[String, String] =
      ProducerRecord(topic.topic, msg.client.requestId, str)
    val records = ProducerRecords.one(record)
    KafkaProducer
      .resource[F, String, String](producerSettings)
      .use { client =>
        for {
          _ <- Logger[F].info(s"Publishing Message to Kafka: ${record.key}")
          res <- client.produce(records)
        } yield res
      }
      .flatten
  }
}
