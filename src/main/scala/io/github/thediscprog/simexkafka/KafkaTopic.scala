package io.github.thediscprog.simexkafka

trait KafkaTopic {
  val name: String
  val topic: String
  val partitions: Int
  val replication: Short
}
