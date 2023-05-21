package consumer

import akka.actor.{ActorRef}
import akka.util.ByteString

import java.util.UUID
import scala.util.Random

object ConsumerDataGenerator {
  def sendData(actorRef: ActorRef) = {
    val listOfTopics: List[String] = List("Speed", "Distance", "Pace")
    val r: Random = new Random
    var currentTopic: String = listOfTopics(r.nextInt(3))
    var message: String = "Consumer" + "\n" + currentTopic + "\n" + UUID.randomUUID().toString
    actorRef ! ByteString(message)
  }
}


