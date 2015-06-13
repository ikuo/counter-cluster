package buffercluster

import akka.actor._

class Frontend extends Actor {
  private val backends = List[ActorRef]()
  var jobCounter = 0

  def receive = {
    case (key: String, value: String) => println(key)
  }
}
