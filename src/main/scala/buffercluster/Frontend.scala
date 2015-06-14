package buffercluster
import akka.cluster._
import akka.cluster.sharding._
import akka.actor._
import ClusterEvent._
import scala.concurrent.duration._
import java.util.UUID

class Frontend extends Actor with ActorLogging {
  implicit val ec = context.dispatcher
  val buffer = ClusterSharding(context.system).shardRegion(Buffer.shardingName)
  var count = 0
  def random: String = UUID.randomUUID.toString

  override def preStart: Unit = {
    context.system.scheduler.schedule(0.millis, 100.millis) {
      buffer ! Buffer.Post(s"key-$random", s"value-$random")
    }
    super.preStart
  }

  def receive = {
    case Frontend.GetCount => sender ! count
  }
}

object Frontend {
  object GetCount
}
