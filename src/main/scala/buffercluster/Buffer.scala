package buffercluster

import akka.actor._
import akka.cluster._
import ClusterEvent._

class Buffer extends Actor with ActorLogging {
  val cluster = Cluster.get(context.system)
  override def preStart: Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop: Unit = cluster.unsubscribe(self)

  def receive = {
    case Buffer.Put(key, value) => log.info(s"Put $key=$value")
    case MemberUp(member) =>
      log.info(s"MemberUp ${member}")
  }
}

object Buffer {
  case class Put(key: String, value: String)
}
