package broker

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.ByteString
import producer.DataGenerator

import scala.collection.mutable.ListBuffer

class ProducerData(topic: String, message: String) {
  var producerTopic: String = topic
  var producerMessage: String = message
}

class ConsumerData(topic: String, ID: String) {
  var consumerTopic: String = topic
  var consumerID: String = ID
}

class ProducerDataHandler extends Actor {
  def receive = {
    case message: String => {
      val splitMessage = message.split("\n")
      if (splitMessage(1).equals("Dead Letter")) {
        DeadLetterChannel.deadLetters += message
        println("\u001B[31m" + "Dead letter sent to the dead letter channel" + "\u001B[0m")
        println
      } else {
        val producerData: ProducerData = new ProducerData(splitMessage(1), splitMessage(2))
        val aggregator = ActorSystem().actorOf(Props(new Aggregator))
        aggregator ! producerData
      }
    }
  }
}

class ConsumerDataHandler extends Actor {
  def receive = {
    case message: String => {
      val splitMessage = message.split("\n")
      val consumerData: ConsumerData = new ConsumerData(splitMessage(1), splitMessage(2))
      ConsumerStorage.consumerStorage += consumerData
      if (ProducerDurableQueue.producerStorage.nonEmpty) {
        for (i <- ProducerDurableQueue.producerStorage.indices) {
          for (j <- ConsumerStorage.consumerStorage.indices) {
            if (ProducerDurableQueue.producerStorage(i).producerTopic.equals(ConsumerStorage.consumerStorage(j).consumerTopic)) {
              val ConsumerServer = DataGenerator.ConsumerTCPServer
              var message: String = ProducerDurableQueue.producerStorage(i).producerTopic + "\n" + ProducerDurableQueue.producerStorage(i).producerMessage + "\n" + "Sent to the consumer: " + ConsumerStorage.consumerStorage(j).consumerID
              ConsumerServer ! ByteString(message)
            }
          }
        }
        ProducerDurableQueue.producerStorage.clear()
      }
    }
  }
}

class Aggregator extends Actor {
  def receive = {
    case producer: ProducerData => {
      if (ConsumerStorage.consumerStorage.isEmpty) {
        ProducerDurableQueue.producerStorage += producer
      } else {
        for(i<-ConsumerStorage.consumerStorage.indices) {
          if(producer.producerTopic.equals(ConsumerStorage.consumerStorage(i).consumerTopic)) {
            val ConsumerServer = DataGenerator.ConsumerTCPServer
            var message: String = producer.producerTopic + "\n" + producer.producerMessage + "\n" + "Sent to the consumer: " + ConsumerStorage.consumerStorage(i).consumerID
            ConsumerServer ! ByteString(message)
          }
        }
      }
    }
  }
}

object ProducerDurableQueue {
  val producerStorage: ListBuffer[ProducerData] = new ListBuffer[ProducerData]
}

object ConsumerStorage {
  val consumerStorage: ListBuffer[ConsumerData] = new ListBuffer[ConsumerData]
}

object DeadLetterChannel {
  val deadLetters: ListBuffer[String] = new ListBuffer[String]
}


