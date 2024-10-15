package io.github.thediscprog.simexkafka.producer

import cats.effect.kernel.Async
import fs2.kafka.ProducerResult
import io.github.thediscprog.simexkafka.KafkaTopic
import io.github.thediscprog.simexkafka.config.KafkaConfig
import io.github.thediscprog.simexmessaging.messaging.Simex
import org.typelevel.log4cats.Logger

trait SimexKafkaProducer[F[_]] {

  def publishMessge(msg: Simex, topic: KafkaTopic): F[ProducerResult[String, String]]
}

object SimexKafkaProducer {

  def apply[F[_]: Async: Logger](kafkaConfig: KafkaConfig) =
    new SimexKafkaFS2Producer[F](kafkaConfig)
}
