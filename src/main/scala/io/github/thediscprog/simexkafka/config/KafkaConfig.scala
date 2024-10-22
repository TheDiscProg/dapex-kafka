package io.github.thediscprog.simexkafka.config

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class KafkaConfig(
    bootstrapServer: String,
    port: Int,
    group: String
)

object KafkaConfig {
  implicit val kafkaConfig: Decoder[KafkaConfig] = deriveDecoder
}
