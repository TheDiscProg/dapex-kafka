package simex.kafka.it

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._
import com.github.dockerjava.api.command.InspectContainerResponse
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import org.typelevel.log4cats.slf4j.Slf4jLogger
import simex.kafka.{KafkaConfigurator, KafkaTopic, consumer, producer}
import simex.kafka.config.KafkaConfig
import simex.kafka.producer.SimexKafkaProducer
import simex.messaging.Simex
import simex.test.SimexTestFixture
import thediscprog.utillibrary.caching.CachingService

import scala.concurrent.duration._
import scala.util.matching.Regex

class KafkaClientLibraryTest
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with SimexTestFixture {

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(30, Seconds), interval = Span(100, Millis))

  private implicit def unsafeLogger = Slf4jLogger.getLogger[IO]

  // Url is in the format PLAINTEXT://localhost:60709
  private val urlPattern: Regex = """^(\w+)://(\w+):(\w+)""".r

  private val dockerImage = DockerImageName.parse("confluentinc/cp-kafka:7.5.1")

  private val container = setUpKafkaContainer()

  private val cachingService = CachingService.cachingService[IO]()

  private val testTopic = new KafkaTopic {
    override val name: String = "Test Topic"
    override val topic: String = "test-topic"
    override val partitions: Int = 1
    override val replication: Short = 1.toShort
  }

  private val request = authenticationRequest

  it should "run kafkain a container" in {
    val info: InspectContainerResponse = container.getContainerInfo
    val bootstrapServersUrl = container.getBootstrapServers

    info.getImageId shouldBe "sha256:9292584eb7981ebf2a0eb1f50307c0b041f15c039e2539533f468136fa6e4d81"

    bootstrapServersUrl match {
      case urlPattern("PLAINTEXT", "localhost", _) => succeed
      case _ => fail
    }
  }

  it should "create topics in a container" in {
    val kafkaConfig = getContainerConfig(container.getBootstrapServers)
    val adminClientResource = KafkaConfigurator.kafkaAdminClientResource[IO](kafkaConfig)

    val topics = (for {
      _ <- KafkaConfigurator.createTopicIfNotExists[IO](testTopic, kafkaConfig)
      topics <- adminClientResource.use { client =>
        client.listTopics.names
      }
    } yield topics).unsafeToFuture()

    whenReady(topics) { ts =>
      ts.contains(testTopic.topic) shouldBe true
    }
  }

  it should "publish messages in Kafka and consume them" in {
    val kafkaConfig = getContainerConfig(container.getBootstrapServers)
    val kafkaProducer = producer.SimexKafkaProducer[IO](kafkaConfig)
    val kafkaConsumer = consumer.SimexKafkaConsumer[IO](kafkaConfig, processMessageFromKafka)
    val keys = (for {
      _ <- Seq.range(0, 10).traverse(i => publishToKafka(kafkaProducer, i))
      _ <- kafkaConsumer
        .consumeFromTopic(testTopic)
        .interruptAfter(5.seconds)
        .compile
        .drain
      keys <- cachingService.getAllKeys
    } yield keys).unsafeToFuture()

    whenReady(keys) { ks =>
      println(ks)
      ks.size shouldBe 10
      ks.contains("producer-1-9") shouldBe true
    }
  }

  private def processMessageFromKafka(msg: Simex): IO[Boolean] =
    for {
      _ <- cachingService.storeInCache(msg.client.requestId, msg)
    } yield true

  private def publishToKafka(kafkaProducer: SimexKafkaProducer[IO], count: Int) = {
    val sentMsg = request.copy(
      client = request.client.copy(requestId = s"producer-1-$count"),
      destination = request.destination.copy(resource = "service.dbwrite")
    )
    kafkaProducer.publishMessge(sentMsg, testTopic)
  }

  private def getContainerConfig(bootstrapServers: String): KafkaConfig = {
    val serverPort = bootstrapServers.split("//")(1)
    val serverTokens = serverPort.split(":")
    val bootstrapServer = serverTokens(0)
    val port = serverTokens(1)
    KafkaConfig(bootstrapServer = bootstrapServer, port = port.toInt, group = "testgroup")
  }

  private def setUpKafkaContainer(): KafkaContainer = {
    val kafkaTestContainer = new KafkaContainer(dockerImage)
      .withKraft()
    kafkaTestContainer.start()
    kafkaTestContainer
  }
}
