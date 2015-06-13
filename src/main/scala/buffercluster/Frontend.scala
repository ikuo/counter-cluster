package buffercluster

import akka.cluster._
import akka.actor._
import ClusterEvent._

class Frontend extends Actor with ActorLogging {
  val cluster = Cluster.get(context.system)
  override def preStart: Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop: Unit = cluster.unsubscribe(self)
  private var buffers: List[ActorSelection] = Nil

  def receive = {
    case msg : Buffer.Put => buffers.foreach(_ ! msg)
    case state: CurrentClusterState => println(s"${state}============================================================")
    case MemberUp(member) if member.hasRole("buffer") =>
      log.info(s"Registering a buffer ${member}")
      val actorRef = context.actorSelection(member.address + "/user/buffer")
      this.buffers = actorRef :: buffers
      dispatch(Buffer.Put("key2", "value2"))
  }

  private def dispatch(msg: Buffer.Put): Unit =
    this.buffers.foreach(_ ! msg)
}
