package dapex.kafka

import cats.effect.Resource
import cats.effect.kernel.Async
import cats.implicits._
import dapex.kafka.config.KafkaConfig
import fs2.kafka.{AdminClientSettings, KafkaAdminClient}
import org.apache.kafka.clients.admin.NewTopic

object KafkaConfigurator {

  def kafkaAdminClientResource[F[_]: Async](
      kafkaConfig: KafkaConfig
  ): Resource[F, KafkaAdminClient[F]] =
    KafkaAdminClient
      .resource(AdminClientSettings(s"${kafkaConfig.bootstrapServer}:${kafkaConfig.port}"))

  def createTopicIfNotExists[F[_]: Async](topic: KafkaTopic, kafkaConfig: KafkaConfig): F[Unit] =
    kafkaAdminClientResource[F](kafkaConfig)
      .use { client =>
        for {
          topics <- client.listTopics.names
          _ <-
            if (topics.contains(topic.topic))
              ().pure[F]
            else
              client.createTopic(new NewTopic(topic.topic, topic.partitions, topic.replication))
        } yield ()
      }
}
