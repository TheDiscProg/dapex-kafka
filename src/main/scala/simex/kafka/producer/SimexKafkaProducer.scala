package simex.kafka.producer

import cats.effect.kernel.Async
import fs2.kafka.ProducerResult
import org.typelevel.log4cats.Logger
import simex.kafka.KafkaTopic
import simex.kafka.config.KafkaConfig
import simex.messaging.Simex

trait SimexKafkaProducer[F[_]] {

  def publishMessge(msg: Simex, topic: KafkaTopic): F[ProducerResult[String, String]]
}

object SimexKafkaProducer {

  def apply[F[_]: Async: Logger](kafkaConfig: KafkaConfig) =
    new SimexKafkaFS2Producer[F](kafkaConfig)
}
