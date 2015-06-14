package buffercluster

import akka.cluster._
import akka.cluster.sharding._
import akka.actor._
import ClusterEvent._

class Frontend extends Actor with ActorLogging {
  val cluster = Cluster.get(context.system)
  override def preStart: Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop: Unit = cluster.unsubscribe(self)
  private var buffers: List[ActorSelection] = Nil

  def receive = {
    case msg : Buffer.Post => buffers.foreach(_ ! msg)
    case MemberUp(member) =>
      // this.buffers = actorRef :: buffers
      // dispatch(Buffer.Post("key2", "value2"))
      log.info(s"MemberUp $member")

      //val actorRef = context.actorSelection(member.address + "/user/buffer")
      //actorRef ! Buffer.Post("key0", "value0")

      val buffer = ClusterSharding(context.system).shardRegion(Buffer.shardingName)
      println(s"${buffer}============================================================")
      buffer ! Buffer.Post(java.util.UUID.randomUUID.toString, "value1")
  }

  private def dispatch(msg: Buffer.Post): Unit =
    this.buffers.foreach(_ ! msg)
}
