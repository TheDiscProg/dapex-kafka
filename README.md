# dapex-kafka
DAPEX messaging using KAFKA

# Using Dapex-Kafka Library
See the `KafkaClientLibraryTest.scala` on how to either publish or consume from Kafka cluster.

## 1. Configuration
The first stage is to create the configurations for bootstrap servers and the Kafka topics.
There are two classes that you will need to set or implement:
* `KafkaConfig.scala`: The configuraton for the Kafka cluster
* `KafkaTopic.scala`: The topics for which the service either publishes to or consumes from

## 2. Create Topic
The second stage is to create the topics if they do not already exist. For this, there is a handy class:
`KafkaConfigurator.scala` that has the following methods:

`  def kafkaAdminClientResource[F[_]: Async](kafkaConfig: KafkaConfig): Resource[F, KafkaAdminClient[F]]`

`  def createTopicIfNotExists[F[_]: Async](topic: KafkaTopic, kafkaConfig: KafkaConfig): F[Unit]`

The admin client resource can be used for carring out further admin tasks. The `createTopicIfNotExists` method
can be used to create topics but only if they do not exist, hence, it is safe to run this each time when the service
starts up.

## Publishing/Producing to Kafka Cluster
To create an instance of a producer, use the following:

`   val kafkaProducer = DapexKafkaProducer[IO](kafkaConfig)`

Once the `kafkaProducer` has been created, then to publish a message:

`   kafkaProducer.publishMessge(dapexMessage, topic)`

which has a return type of ` F[ProducerResult[String, String]]` which can be further queried for producing/publishing
results.

## Consuming from Kafka
To consume from Kafka topics, create an instance of `DapexKafkaConsumer`:

`   val kafkaConsumer = DapexKafkaConsumer[IO](kafkaConfig, processMessageFromKafka)`

The final argument is a function with the following signature:
```scala
    (msg:DapexMessage) => F[Boolean]
```

The return indicates whether to acknowledge Kafka that the message was processed. A `false` return indicates that the 
consumer should not acknowledge Kafka, so that another consumer can handle it, or handle the message later. Effecively, 
it doesn't move the offset along.

This class has the following method:
```scala
def consumeFromTopic(topic: KafkaTopic): Stream[F, Unit]
```
The stream is a FS2 Stream that can be joined with other streams to process concurrently form multiple topics.

As a guide, create an instance for each topic.