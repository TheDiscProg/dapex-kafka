package dapex.kafka.consumer

import cats.effect.kernel.Async
import dapex.kafka.KafkaTopic
import dapex.kafka.config.KafkaConfig
import dapex.messaging.DapexMessage
import fs2.Stream
import org.typelevel.log4cats.Logger

trait DapexKafkaConsumer[F[_]] {

  def consumeFromTopic(topic: KafkaTopic): Stream[F, Unit]
}

object DapexKafkaConsumer {
  def apply[F[_]: Async: Logger](kafkaConfig: KafkaConfig, f: DapexMessage => F[Boolean]) =
    new DapexKafkaFS2Consumer[F](kafkaConfig, f)
}
