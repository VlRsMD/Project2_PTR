package connection

import akka.actor.{Actor, Props}
import akka.io.{IO, Tcp}
import akka.io.Tcp.{Bind, Bound, Connected, ConnectionClosed, Received, Register, Write}
import akka.util.ByteString
import broker.{ConsumerDataHandler, ProducerDataHandler}

import java.net.InetSocketAddress

class TCPConnectionManager(address: String, port: Int) extends Actor {
  import context.system
  IO(Tcp) ! Bind(self, new InetSocketAddress(address, port))

  override def receive: Receive = {
    case Bound(local) =>
      if(port == 7070) {
        println(s"Producer server started on $local")
        println
      }
      if(port == 7071) {
        println(s"Broker server started on $local")
        println
      }
      if(port == 7072) {
        println(s"Consumer server started on $local")
        println
      }
    case Connected(remote, local) =>
      val handler = context.actorOf(Props[TCPConnectionHandler])
      println(s"New connnection: $local -> $remote")
      println
      sender() ! Register(handler)
    case message: ByteString =>
      Write(message)
      val decodedMessage = message.utf8String
      if(port == 7071) {
        val splitMessage = decodedMessage.split("\n")
        if (splitMessage(0).equals("Producer")) {
          val producerDataHandler = system.actorOf(Props(new ProducerDataHandler))
          producerDataHandler ! decodedMessage
          println("\u001B[34m" + decodedMessage + "\u001B[0m")
          println
        }
        if (splitMessage(0).equals("Consumer")) {
          val consumerDataHandler = system.actorOf(Props(new ConsumerDataHandler))
          consumerDataHandler ! decodedMessage
          println("\u001B[36m" + decodedMessage + "\u001B[0m")
          println
        }
      }
      if(port == 7072) {
        println("\u001B[33m" + decodedMessage + "\u001B[0m")
        println
      }
  }
}

class TCPConnectionHandler extends Actor {
  override def receive: Actor.Receive = {
    case Received(data) =>
      val decoded = data.utf8String
      sender() ! Write(ByteString(s" $decoded"))
    case message: ConnectionClosed => {
      println("Connection has been closed")
      context stop self
    }
  }
}
