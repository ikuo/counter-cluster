package buffercluster

import akka.actor._
import akka.cluster._
import ClusterEvent._

class Buffer extends Actor {
  val cluster = Cluster.get(context.system)
  override def preStart: Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop: Unit = cluster.unsubscribe(self)

  def receive = {
    case (key: String, value: String) => println(key)
    case state: CurrentClusterState => println(s"${state}============================================================")
    case MemberUp(member) =>
      println(s"MemberUp ${member}")
  }
}

object Buffer {
  case class Put(key: String, value: String)
}
