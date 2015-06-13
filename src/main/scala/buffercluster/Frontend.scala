package buffercluster

import akka.actor._

class Frontend extends Actor {
  private val backends = List[ActorRef]()
  var jobCounter = 0

  override def receive = {
    case (key: String, value: String) => println(key)
    case _ => println("no-match")
  }
}

object Frontend {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("frontend")
    //val frontend = system.actorOf(Props(classOf[Frontend]), "frontend")
    //frontend ! (("key1", "value1"))
  }
}
