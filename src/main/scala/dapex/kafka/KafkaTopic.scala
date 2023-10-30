package dapex.kafka

trait KafkaTopic {
  val name: String
  val topic: String
  val partitions: Int
  val replication: Short
}

case object KafkaTopic {

  case object DB_WRITE extends KafkaTopic {
    override val name: String = "Database Writer Queue"
    override val topic: String = "db-write"
    override val partitions: Int = 3
    override val replication: Short = 2.toShort
  }

}
