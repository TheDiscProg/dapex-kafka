package dapex.kafka.producer

import cats.effect.kernel.Async
import dapex.kafka.KafkaTopic
import dapex.kafka.config.KafkaConfig
import dapex.messaging.DapexMessage
import fs2.kafka.ProducerResult
import org.typelevel.log4cats.Logger

trait DapexKafkaProducer[F[_]] {

  def publishMessge(msg: DapexMessage, topic: KafkaTopic): F[ProducerResult[String, String]]
}

object DapexKafkaProducer {

  def apply[F[_]: Async: Logger](kafkaConfig: KafkaConfig) =
    new DapexKafkaFS2Producer[F](kafkaConfig)
}
