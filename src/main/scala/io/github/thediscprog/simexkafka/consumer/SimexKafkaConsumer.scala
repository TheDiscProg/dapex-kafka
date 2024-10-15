package io.github.thediscprog.simexkafka.consumer

import cats.effect.kernel.Async
import fs2.Stream
import io.github.thediscprog.simexkafka.KafkaTopic
import io.github.thediscprog.simexkafka.config.KafkaConfig
import io.github.thediscprog.simexmessaging.messaging.Simex
import org.typelevel.log4cats.Logger

trait SimexKafkaConsumer[F[_]] {

  def consumeFromTopic(topic: KafkaTopic): Stream[F, Unit]
}

object SimexKafkaConsumer {
  def apply[F[_]: Async: Logger](kafkaConfig: KafkaConfig, f: Simex => F[Boolean]) =
    new SimexKafkaFS2Consumer[F](kafkaConfig, f)
}
