package dapex.kafka.consumer

import cats.effect._
import cats.syntax.all._
import dapex.entities.ConversionError
import dapex.kafka.KafkaTopic
import dapex.kafka.config.KafkaConfig
import fs2.kafka._
import dapex.messaging.DapexMessage
import fs2.kafka.{AutoOffsetReset, ConsumerSettings, KafkaConsumer}
import fs2._
import org.typelevel.log4cats.Logger

class DapexKafkaFS2Consumer[F[_]: Async: Logger](
    kafkaConfig: KafkaConfig,
    f: DapexMessage => F[Boolean]
) extends DapexKafkaConsumer[F] {

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
    val msg: Either[ConversionError, DapexMessage] =
      DapexMessage.deSerializeFromString(record.value)
    msg.fold(
      err =>
        Logger[F].warn(s"Message could not be transformed from string: ${err.message}") *> false
          .pure[F],
      dapex => f(dapex)
    )

  }
}
