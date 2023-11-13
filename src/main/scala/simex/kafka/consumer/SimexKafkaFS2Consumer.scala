package simex.kafka.consumer

import cats.effect._
import cats.syntax.all._
import simex.entities.ConversionError
import fs2.kafka._
import fs2.kafka.{AutoOffsetReset, ConsumerSettings, KafkaConsumer}
import fs2._
import org.typelevel.log4cats.Logger
import simex.kafka.KafkaTopic
import simex.kafka.config.KafkaConfig
import simex.messaging.Simex

class SimexKafkaFS2Consumer[F[_]: Async: Logger](
    kafkaConfig: KafkaConfig,
    f: Simex => F[Boolean]
) extends SimexKafkaConsumer[F] {

  val consumerSettings =
    ConsumerSettings[F, String, String]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers(s"${kafkaConfig.bootstrapServer}:${kafkaConfig.port}")
      .withGroupId(kafkaConfig.group)

  override def consumeFromTopic(topic: KafkaTopic): Stream[F, Unit] =
    KafkaConsumer
      .stream(consumerSettings)
      .subscribeTo(topic.topic)
      .records
      .mapAsync(1) { commitable =>
        for {
          success <- processRecord(commitable.record)
          _ <-
            if (success) {
              Logger[F].debug(s"Committing offset for ${commitable.offset}") *>
                commitable.offset.commit
            } else
              Logger[F].debug(
                s"Consuming record failed for offset: [${commitable.offset}]: [${commitable.record}]"
              )
        } yield ()
      }

  private def processRecord(record: ConsumerRecord[String, String]): F[Boolean] = {
    val msg: Either[ConversionError, Simex] =
      Simex.deSerializeFromString(record.value)
    msg.fold(
      err =>
        Logger[F].warn(s"Message could not be transformed from string: ${err.message}") *> false
          .pure[F],
      dapex => f(dapex)
    )

  }
}
