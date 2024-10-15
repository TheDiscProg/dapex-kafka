package io.github.thediscprog.simexkafka.config

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder
import pureconfig.{CamelCase, ConfigFieldMapping}
import pureconfig.generic.ProductHint

case class KafkaConfig(
    bootstrapServer: String,
    port: Int,
    group: String
)

object KafkaConfig {
  implicit val hint: ProductHint[KafkaConfig] =
    ProductHint[KafkaConfig](ConfigFieldMapping(CamelCase, CamelCase))
  implicit val kafkaConfig: Decoder[KafkaConfig] = deriveDecoder
}
