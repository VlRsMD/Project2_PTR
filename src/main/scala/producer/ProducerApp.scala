package producer

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString
import connection.TCPConnectionManager

import scala.util.Random

object DataGenerator {
  val ProducerTCPServer: ActorRef = ActorSystem().actorOf(Props(classOf[TCPConnectionManager], "localhost", 7070))
  val BrokerTCPServer: ActorRef = ActorSystem().actorOf(Props(classOf[TCPConnectionManager], "localhost", 7071))
  val ConsumerTCPServer: ActorRef = ActorSystem().actorOf(Props(classOf[TCPConnectionManager], "localhost", 7072))
  var counter: Int = 0
  def sendData = {
    val listOfTopics: List[String] = List("Speed", "Distance", "Pace")
    val r: Random = new Random
    while(true) {
      counter = counter+1
      var currentTopic: String = listOfTopics(r.nextInt(3))
      var currentMessage: String = null
      if (currentTopic.equals("Speed")) {
        currentMessage = "Current speed is " + r.nextInt(30) + " km/h"
      }
      if (currentTopic.equals("Distance")) {
        currentMessage = "Current distance is " + r.nextInt(10000) + " m"
      }
      if (currentTopic.equals("Pace")) {
        currentMessage = "Current pace is " + r.nextInt(10) + " min/km"
      }
      var message: String = null
      if (counter==10) {
        message = "Producer" + "\n" + "Dead Letter"
      } else {
        message = "Producer" + "\n" + currentTopic + "\n" + currentMessage
      }
      BrokerTCPServer ! ByteString(message)
      Thread.sleep(500)
      if (counter==3 || counter==4 || counter==5 || counter==6 || counter==7 || counter%5==0) {
        consumer.ConsumerDataGenerator.sendData(BrokerTCPServer)
        Thread.sleep(500)
      }
    }
  }
}

object Producer extends App {
  DataGenerator.sendData
}
