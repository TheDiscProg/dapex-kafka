package simex.kafka.producer

import cats.effect.kernel.Async
import cats.syntax.all._
import fs2.kafka._
import org.typelevel.log4cats.Logger
import simex.kafka.KafkaTopic
import simex.kafka.config.KafkaConfig
import simex.messaging.Simex

class SimexKafkaFS2Producer[F[_]: Async: Logger](kafkaConfig: KafkaConfig)
    extends SimexKafkaProducer[F] {

  val producerSettings: ProducerSettings[F, String, String] =
    ProducerSettings[F, String, String]
      .withBootstrapServers(s"${kafkaConfig.bootstrapServer}:${kafkaConfig.port}")

  override def publishMessge(
      msg: Simex,
      topic: KafkaTopic
  ): F[ProducerResult[String, String]] = {
    val str = Simex.serializeToString(msg)
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
