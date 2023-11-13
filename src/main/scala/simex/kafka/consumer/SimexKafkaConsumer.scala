package simex.kafka.consumer

import cats.effect.kernel.Async
import fs2.Stream
import org.typelevel.log4cats.Logger
import simex.kafka.KafkaTopic
import simex.kafka.config.KafkaConfig
import simex.messaging.Simex

trait SimexKafkaConsumer[F[_]] {

  def consumeFromTopic(topic: KafkaTopic): Stream[F, Unit]
}

object SimexKafkaConsumer {
  def apply[F[_]: Async: Logger](kafkaConfig: KafkaConfig, f: Simex => F[Boolean]) =
    new SimexKafkaFS2Consumer[F](kafkaConfig, f)
}
